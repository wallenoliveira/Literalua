// src/main/java/br/com/alura/literalura/model/DadosAutores.java
package br.com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosAutores(
        @JsonAlias("name") String nome, // Corrigido para "name" (minúsculo)
        @JsonAlias("birth_year") Integer anoNascimento, // Corrigido para Integer (pode ser nulo)
        @JsonAlias("death_year") Integer anoFalecimento) { // Corrigido para Integer (pode ser nulo)

    // Método para verificar se o autor estava vivo em um determinado ano.
    // Mova este método para a entidade Autor se for para a lógica de negócio do banco de dados.
    // Se for apenas para filtragem temporária dos dados da API, pode manter aqui.
    public boolean estaVivoEm(int ano) {
        return (anoNascimento != null && anoNascimento <= ano) &&
                (anoFalecimento == null || anoFalecimento > ano);
    }
}