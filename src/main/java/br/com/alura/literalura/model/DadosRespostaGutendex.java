// src/main/java/br/com/alura/literalura/model/DadosRespostaGutendex.java
package br.com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// Ignora quaisquer outros campos na resposta JSON que não mapeamos
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosRespostaGutendex(
        @JsonAlias("count") Long count,
        @JsonAlias("next") String next,
        @JsonAlias("previous") String previous,
        @JsonAlias("results") List<DadosLivros> resultados // <--- ESTE NOME É CRUCIAL!
) {
    // Construtor canônico compacto. Garante que a lista de resultados não seja nula.
    public DadosRespostaGutendex {
        if (resultados == null) {
            resultados = List.of(); // Usa List.of() para uma lista imutável vazia
        }
    }
}