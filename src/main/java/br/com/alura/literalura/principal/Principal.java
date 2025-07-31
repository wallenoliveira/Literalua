// src/main/java/br/com/alura/literalura/principal/Principal.java
package br.com.alura.literalura.principal; // PACOTE EXATO DA SUA IMAGEM

import br.com.alura.literalura.entity.Autor;
import br.com.alura.literalura.entity.Livro;
import br.com.alura.literalura.model.Idioma;
import br.com.alura.literalura.service.CatalogoLivrosService; // Importa o novo serviço de catálogo

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component // Marca como componente Spring
public class Principal implements CommandLineRunner { // Renomeado de AplicacaoCatalogo para Principal

    @Autowired
    private CatalogoLivrosService catalogoService; // Injeta o serviço de catálogo

    private Scanner leitura = new Scanner(System.in);

    @Override
    public void run(String... args) throws Exception {
        exibeMenu();
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 0) {
            var menu = """
                    ********** CATÁLOGO DE LIVROS **********
                    
                    1- Buscar Livro pelo título 
                    2- Listar livros registrados
                    3- Listar autores registrados 
                    4- Listar autores vivos em um determinado ano
                    5- Listar livros em um determinado idioma 
                    6- Top 10 dos livros mais baixados
                    7- Buscar autor pelo nome 
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            System.out.print("Escolha o número de sua opção: ");
            opcao = leitura.nextInt();
            leitura.nextLine(); // Consome a nova linha

            switch (opcao) {
                case 1:
                    System.out.print("Digite o título do livro para buscar na API e salvar: ");
                    String tituloBusca = leitura.nextLine();
                    Livro livroSalvo = catalogoService.buscarESalvarLivro(tituloBusca);
                    if (livroSalvo != null) {
                        System.out.println("\n--- Livro Processado ---");
                        System.out.println(livroSalvo);
                    }
                    break;
                case 2:
                    System.out.println("\n--- Livros Registrados no Banco de Dados ---");
                    List<Livro> livrosDoDb = catalogoService.listarTodosLivros();
                    if (livrosDoDb.isEmpty()) {
                        System.out.println("Nenhum livro registrado no banco de dados ainda.");
                    } else {
                        livrosDoDb.forEach(System.out::println);
                    }
                    long totalLivros = catalogoService.contarTotalLivros(); // Chama o método de contagem
                    System.out.println(String.format("\nTotal de livros registrados até o momento: %d", totalLivros)); // Imprime o total
                    break;
                case 3:
                    System.out.println("\n--- Autores Registrados no Banco de Dados ---");
                    List<Autor> autoresDoDb = catalogoService.listarTodosAutoresComLivros();
                    if (autoresDoDb.isEmpty()) {
                        System.out.println("Nenhum autor registrado no banco de dados ainda.");
                    } else {
                        autoresDoDb.forEach(autor -> {
                            System.out.println("Autor: " + autor.getNome());
                            System.out.println("Ano de nascimento: " + (autor.getAnoNascimento() != null ? autor.getAnoNascimento() : "N/D"));
                            System.out.println("Ano de falecimento: " + (autor.getAnoFalecimento() != null ? autor.getAnoFalecimento() : "Vivo"));

                            System.out.println("Livros: " + (autor.getLivros().isEmpty() ? "Nenhum" : autor.getLivros().stream().map(Livro::getTitulo).collect(Collectors.joining(", "))));
                            System.out.println();
                        });
                    }
                    long totalAutores = catalogoService.contarTotalAutores(); // Chama o método de contagem
                    System.out.println(String.format("\nTotal de autores registrados até o momento: %d", totalAutores)); // Imprime o total
                    break;
                case 4:
                    System.out.print("Digite o ano para verificar autores vivos no banco de dados: ");
                    int anoBusca = leitura.nextInt();
                    leitura.nextLine(); // Consome a nova linha

                    System.out.println("\n--- Autores Vivos em " + anoBusca + " (do Banco de Dados) ---");
                    List<Autor> autoresVivosDoDb = catalogoService.listarAutoresVivosEmAno(anoBusca);
                    if (autoresVivosDoDb.isEmpty()) {
                        System.out.println("Nenhum autor encontrado vivo neste ano no banco de dados.");
                    } else {
                        autoresVivosDoDb.forEach(autor -> {
                            System.out.println("Autor: " + autor.getNome());
                            System.out.println("Ano de nascimento: " + (autor.getAnoNascimento() != null ? autor.getAnoNascimento() : "N/D"));
                            System.out.println("Ano de falecimento: " + (autor.getAnoFalecimento() != null ? autor.getAnoFalecimento() : "Vivo"));
                            System.out.println("Livros: " + (autor.getLivros().isEmpty() ? "Nenhum" : autor.getLivros().stream().map(Livro::getTitulo).collect(Collectors.joining(", "))));
                            System.out.println();
                        });
                    }
                    break;
                case 5:
                    System.out.println("\nInsira o idioma para realizar a busca (do Banco de Dados):");
                    for (Idioma i : Idioma.values()) {
                        System.out.println(i.getCodigoGutendex() + "- " + i.getNomeCompleto());
                    }
                    System.out.print("Sua escolha (ex: 'es', 'pt'): ");
                    String escolhaIdiomaInput = leitura.nextLine();

                    Idioma idiomaSelecionado = Idioma.fromCodigoGutendex(escolhaIdiomaInput);

                    if (idiomaSelecionado != null) {
                        System.out.println("\n--- Livros em " + idiomaSelecionado.getNomeCompleto() + " (do Banco de Dados) ---");
                        List<Livro> livrosPorIdiomaDoDb = catalogoService.listarLivrosPorIdioma(idiomaSelecionado);
                        if (livrosPorIdiomaDoDb.isEmpty()) {
                            System.out.println("Nenhum livro encontrado neste idioma no banco de dados.");
                        } else {
                            livrosPorIdiomaDoDb.forEach(System.out::println);
                        }
                    } else {
                        System.out.println("Idioma inválido. Por favor, escolha uma das opções.");
                    }
                    break;
                case 6: // NOVO CASE PARA O TOP 10
                    System.out.println("\n--- Top 10 Livros Mais Baixados ---");
                    List<Livro> top10Livros = catalogoService.buscarTop10LivrosMaisBaixados();
                    if (top10Livros.isEmpty()) {
                        System.out.println("Nenhum livro encontrado no Top 10. Popule o banco de dados primeiro.");
                    } else {
                        top10Livros.forEach(System.out::println);
                    }
                    break;
                    case 7:
                    System.out.print("Digite o nome ou parte do nome do autor para buscar no banco de dados: ");
                    String nomeAutorBusca = leitura.nextLine();
                    System.out.println("\n--- Autores encontrados com '" + nomeAutorBusca + "' ---");
                    List<Autor> autoresEncontrados = catalogoService.buscarAutorPorNome(nomeAutorBusca);
                    if (autoresEncontrados.isEmpty()) {
                        System.out.println("Nenhum autor encontrado com este nome no banco de dados.");
                    } else {
                        autoresEncontrados.forEach(autor -> {
                            System.out.println("Autor: " + autor.getNome());
                            System.out.println("Ano de nascimento: " + (autor.getAnoNascimento() != null ? autor.getAnoNascimento() : "N/D"));
                            System.out.println("Ano de falecimento: " + (autor.getAnoFalecimento() != null ? autor.getAnoFalecimento() : "Vivo"));
                            System.out.println("Livros: " + (autor.getLivros().isEmpty() ? "Nenhum" : autor.getLivros().stream().map(Livro::getTitulo).collect(Collectors.joining(", "))));
                            System.out.println();
                        });
                    }
                    break;
                case 0:
                    System.out.println("Saindo do catálogo. Até mais!");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
            if (opcao != 0) {
                System.out.println("\nPressione Enter para continuar...");
                leitura.nextLine();
            }
        }

    }
}