package br.com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
// import java.util.Map; // REMOVER ESTA IMPORTAÇÃO SE NÃO FOR USADA EM OUTRO LUGAR

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLivros( // Se você usa record
                           @JsonAlias("title") String titulo,
                           @JsonAlias("authors") List<DadosAutores> autores,
                           @JsonAlias("languages") List<String> idiomas,
                           @JsonAlias("download_count") Integer numeroDownloads
                           // REMOVER ESTE CAMPO: @JsonAlias("formats") Map<String, String> formats
) {
}