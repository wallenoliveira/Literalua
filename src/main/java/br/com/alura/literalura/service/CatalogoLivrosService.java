package br.com.alura.literalura.service;

import br.com.alura.literalura.entity.Autor;
import br.com.alura.literalura.entity.Livro;
import br.com.alura.literalura.model.DadosAutores;
import br.com.alura.literalura.model.DadosLivros;
import br.com.alura.literalura.model.DadosRespostaGutendex;
import br.com.alura.literalura.model.Idioma;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList; // Adicione esta importação se ainda não tiver
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CatalogoLivrosService {

    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private AutorRepository autorRepository;
    @Autowired
    private ConsumoApi consumoApi;

    @Transactional
    public Livro buscarESalvarLivro(String titulo) {
        List<Livro> livrosExistentes = livroRepository.findByTituloContainsIgnoreCase(titulo);
        if (!livrosExistentes.isEmpty()) {
            System.out.println("Livro '" + titulo + "' já existe no banco de dados. Pulando.");
            return livrosExistentes.get(0);
        }

        List<DadosLivros> dadosLivrosList = consumoApi.buscarLivroPorTitulo(titulo);

        if (dadosLivrosList == null || dadosLivrosList.isEmpty()) {
            System.out.println("Livro '" + titulo + "' não encontrado na API Gutendex.");
            return null;
        }

        DadosLivros dadosLivro = dadosLivrosList.get(0);

        Autor autor = null;
        if (dadosLivro.autores() != null && !dadosLivro.autores().isEmpty()) {
            DadosAutores dadosAutor = dadosLivro.autores().get(0);
            Optional<Autor> autorExistente = autorRepository.findByNomeContainsIgnoreCase(dadosAutor.nome());

            if (autorExistente.isPresent()) {
                autor = autorExistente.get();
            } else {
                autor = new Autor(dadosAutor);
                // Não é necessário salvar o autor aqui se o cascade já estiver configurado corretamente no Livro.
                // Mas se você quiser ter certeza que o autor existe antes de associar o livro, pode manter.
                autorRepository.save(autor); // Mantido por segurança, se sua lógica atual precisar
                System.out.println("Autor '" + autor.getNome() + "' salvo no banco de dados.");
            }
        } else {
            System.out.println("Livro '" + dadosLivro.titulo() + "' sem autor definido na API.");
            return null;
        }

        Livro novoLivro = new Livro(dadosLivro);
        novoLivro.setAutor(autor);
        livroRepository.save(novoLivro); // Salva o livro, o que deve persistir o autor se for novo devido ao cascade
        System.out.println("Livro '" + novoLivro.getTitulo() + "' salvo no banco de dados.");

        // CORREÇÃO AQUI: Inicialize com ArrayList<Livro>
        if (autor.getLivros() == null) {
            autor.setLivros(new ArrayList<Livro>()); // Garante que a lista não é nula antes de adicionar
        }
        autor.adicionarLivro(novoLivro); // Este método deve adicionar 'novoLivro' à lista 'livros' do autor
        autorRepository.save(autor); // Salva o autor novamente para garantir que a coleção de livros seja atualizada no DB

        return novoLivro;
    }

    // ... (restante do código permanece o mesmo)

    public List<Livro> listarTodosLivros() {
        return livroRepository.findAll();
    }

    public List<Autor> listarTodosAutoresComLivros() {
        return autorRepository.findAllByOrderByNomeAsc();
    }

    public List<Autor> listarAutoresVivosEmAno(int ano) {
        return autorRepository.buscarAutoresVivosEmAno(ano);
    }

    public List<Livro> listarLivrosPorIdioma(Idioma idioma) {
        return livroRepository.findByIdioma(idioma);
    }

    public List<Livro> buscarTop10LivrosMaisBaixados() {
        return livroRepository.findTop10ByOrderByNumeroDownloadsDesc();
    }

    @Transactional
    public void popularBancoDeDados(int numeroDePaginas) {
        System.out.println("\nIniciando a popularização do banco de dados...");
        for (int i = 1; i <= numeroDePaginas; i++) {
            System.out.println("Buscando livros da página: " + i);
            DadosRespostaGutendex respostaPagina = consumoApi.buscarLivrosPorPagina(i);
            if (respostaPagina != null && respostaPagina.resultados() != null && !respostaPagina.resultados().isEmpty()) {
                for (DadosLivros dadosLivro : respostaPagina.resultados()) {
                    try {
                        buscarESalvarLivro(dadosLivro.titulo());
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Popularização interrompida. Erro: " + e.getMessage());
                        return;
                    } catch (Exception e) {
                        System.err.println("Erro ao processar livro '" + dadosLivro.titulo() + "': " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Nenhum resultado na página " + i + " ou limite de páginas atingido na API.");
                break;
            }
        }
        System.out.println("\nPopularização do banco de dados finalizada.");
    }

    public long contarTotalLivros() {
        return livroRepository.count();
    }

    public long contarTotalAutores() {
        return autorRepository.count();
    }

    @Transactional
    public List<Autor> buscarAutorPorNome(String nome) {
        System.out.println("DEBUG: Iniciando busca por autor: '" + nome + "'");

        List<Autor> autoresEncontradosDb = autorRepository.findByNomeContainingIgnoreCase(nome);

        if (!autoresEncontradosDb.isEmpty()) {
            System.out.println("DEBUG: Autor(es) encontrado(s) no banco de dados local. Retornando do DB.");
            autoresEncontradosDb.forEach(a -> System.out.println("  Autor DB: " + a.getNome()));
            return autoresEncontradosDb;
        } else {
            System.out.println("DEBUG: Autor(es) NÃO encontrado(s) no banco de dados local. Buscando na API Gutendex...");
            DadosRespostaGutendex respostaApi = consumoApi.buscarLivrosPorConsulta(nome);

            Set<Autor> autoresProcessados = new HashSet<>();

            if (respostaApi != null && respostaApi.resultados() != null && !respostaApi.resultados().isEmpty()) {
                System.out.println("DEBUG: Livros encontrados na API para a consulta '" + nome + "'. Total de resultados: " + respostaApi.resultados().size());
                for (DadosLivros dadosLivro : respostaApi.resultados()) {
                    System.out.println("DEBUG: Processando livro da API: " + dadosLivro.titulo());
                    if (dadosLivro.autores() != null && !dadosLivro.autores().isEmpty()) {
                        for (DadosAutores dadosAutorApi : dadosLivro.autores()) {
                            System.out.println("DEBUG:   Verificando autor do livro '" + dadosLivro.titulo() + "': " + dadosAutorApi.nome());
                            if (dadosAutorApi.nome().toLowerCase().contains(nome.toLowerCase())) {
                                System.out.println("DEBUG:     Nome do autor da API ('" + dadosAutorApi.nome() + "') corresponde à busca.");
                                try {
                                    Livro livroSalvo = buscarESalvarLivro(dadosLivro.titulo());
                                    if (livroSalvo != null && livroSalvo.getAutor() != null) {
                                        autoresProcessados.add(livroSalvo.getAutor());
                                        System.out.println("DEBUG:       Livro '" + dadosLivro.titulo() + "' e Autor '" + livroSalvo.getAutor().getNome() + "' processados/salvos.");
                                    } else {
                                        System.out.println("DEBUG:       Livro '" + dadosLivro.titulo() + "' NÃO foi salvo ou autor é nulo.");
                                    }
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    System.err.println("Erro: Busca na API interrompida ao processar livro. " + e.getMessage());
                                    return new ArrayList<>(autoresProcessados);
                                } catch (Exception e) {
                                    System.err.println("Erro inesperado ao processar livro da API para autor '" + nome + "': " + e.getMessage());
                                    e.printStackTrace();
                                }
                            } else {
                                System.out.println("DEBUG:     Nome do autor da API ('" + dadosAutorApi.nome() + "') NÃO corresponde à busca.");
                            }
                        }
                    } else {
                        System.out.println("DEBUG:   Livro '" + dadosLivro.titulo() + "' não possui informações de autor na API.");
                    }
                }
                if (!autoresProcessados.isEmpty()) {
                    System.out.println("DEBUG: Autor(es) encontrado(s) na API e adicionado(s) ao banco de dados. Retornando autores processados.");
                    return new ArrayList<>(autoresProcessados);
                }
            } else {
                System.out.println("DEBUG: API Gutendex não retornou resultados para a consulta '" + nome + "'.");
            }
            System.out.println("DEBUG: Autor(es) não encontrado(s) na API Gutendex ou nenhum autor correspondente foi salvo.");
            return new ArrayList<>();
        }
    }
}