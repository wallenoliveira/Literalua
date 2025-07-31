// src/main/java/br/com/alura/literalura/service/ConsumoApi.java
package br.com.alura.literalura.service;

import br.com.alura.literalura.model.DadosAutores;
import br.com.alura.literalura.model.DadosLivros;
import br.com.alura.literalura.model.DadosRespostaGutendex;
import br.com.alura.literalura.model.Idioma;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ConsumoApi {
    private static final String BASE_URL = "https://gutendex.com/books/";
    private final HttpClient httpClient;

    @Autowired
    private IConverteDados conversor;

    public ConsumoApi() {
        this.httpClient = HttpClient.newHttpClient();
    }

    private DadosRespostaGutendex fazerRequisicao(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return conversor.obterDados(response.body(), DadosRespostaGutendex.class);
            } else {
                System.err.println("Erro ao acessar a API Gutendex. Status: " + response.statusCode());
                // Garante que o construtor é chamado com os tipos corretos
                return new DadosRespostaGutendex(0L, null, null, List.of());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exceção ao buscar dados da API: " + e.getMessage());
            e.printStackTrace();
            // Garante que o construtor é chamado com os tipos corretos
            return new DadosRespostaGutendex(0L, null, null, List.of());
        } catch (RuntimeException e) {
            System.err.println("Erro ao converter resposta da API: " + e.getMessage());
            e.printStackTrace();
            return new DadosRespostaGutendex(0L, null, null, List.of());
        }
    }

    // Este método já retornava List<DadosLivros>, vamos manter assim e ajustar o Service
    public List<DadosLivros> buscarLivroPorTitulo(String titulo) {
        String encodedTitulo = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
        String url = BASE_URL + "?search=" + encodedTitulo;
        System.out.println("Buscando livros na API (por título): " + url);
        DadosRespostaGutendex resposta = fazerRequisicao(url);
        return resposta.resultados();
    }

    // NOVO MÉTODO: Adicionado para a busca geral de livros/autores na API (usado pelo CatalogoLivrosService.buscarAutorPorNome)
    public DadosRespostaGutendex buscarLivrosPorConsulta(String consulta) {
        String encodedConsulta = URLEncoder.encode(consulta, StandardCharsets.UTF_8);
        String url = BASE_URL + "?search=" + encodedConsulta;
        System.out.println("DEBUG: Buscando na API por consulta geral: " + url);
        return fazerRequisicao(url);
    }

    public List<DadosLivros> listarLivrosPorIdioma(Idioma idioma) {
        String url = BASE_URL + "?languages=" + idioma.getCodigoGutendex();
        System.out.println("Buscando livros na API (por idioma): " + url);
        DadosRespostaGutendex resposta = fazerRequisicao(url);
        return resposta.resultados();
    }

    public record DadosAutoresComLivros(DadosAutores autor, List<String> titulosLivros) {}

    public List<DadosAutoresComLivros> listarTodosAutoresComLivros() {
        System.out.println("Buscando uma amostra de livros da API para listar autores...");
        String url = BASE_URL + "?page=1";
        DadosRespostaGutendex resposta = fazerRequisicao(url);

        var autoresComLivros = new ArrayList<DadosAutoresComLivros>();
        Set<DadosAutores> autoresUnicos = new HashSet<>();

        if (resposta != null && resposta.resultados() != null) {
            for (DadosLivros livro : resposta.resultados()) {
                if (livro.autores() != null) {
                    for (DadosAutores autor : livro.autores()) {
                        autoresUnicos.add(autor);
                    }
                }
            }

            for (DadosAutores autor : autoresUnicos) {
                List<String> titulosDoAutor = resposta.resultados().stream()
                        .filter(livro -> livro.autores() != null && livro.autores().contains(autor))
                        .map(DadosLivros::titulo)
                        .collect(Collectors.toList());
                autoresComLivros.add(new DadosAutoresComLivros(autor, titulosDoAutor));
            }
        }

        return autoresComLivros.stream()
                .sorted((a1, a2) -> a1.autor().nome().compareToIgnoreCase(a2.autor().nome()))
                .collect(Collectors.toList());
    }

    public List<DadosAutoresComLivros> listarAutoresVivosEmAno(int ano) {
        return listarTodosAutoresComLivros().stream()
                .filter(autorComLivros -> autorComLivros.autor().estaVivoEm(ano))
                .collect(Collectors.toList());
    }

    public DadosRespostaGutendex buscarLivrosPorPagina(int pageNumber) {
        String url = BASE_URL + "?page=" + pageNumber;
        System.out.println("Buscando página " + pageNumber + " da API: " + url);
        return fazerRequisicao(url);
    }
}