// src/main/java/br/com/alura/literalura/model/Idioma.java
package br.com.alura.literalura.model;

public enum Idioma {
    PORTUGUES("pt", "Português"),
    INGLES("en", "Inglês"),
    ESPANHOL("es", "Espanhol"),
    FRANCES("fr", "Francês"),
    ALEMAO("de", "Alemão"),
    ITALIANO("it", "Italiano"),
    RUSSO("ru", "Russo"),
    CHINES("zh", "Chinês"),
    JAPONES("ja", "Japonês");

    private String codigoGutendex;
    private String nomeCompleto;

    Idioma(String codigoGutendex, String nomeCompleto) {
        this.codigoGutendex = codigoGutendex;
        this.nomeCompleto = nomeCompleto;
    }

    public String getCodigoGutendex() {
        return codigoGutendex;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    // Método estático para obter o Idioma a partir do código da Gutendex
    public static Idioma fromCodigoGutendex(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.codigoGutendex.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        // Se nenhum corresponder, pode retornar null ou lançar uma exceção
        System.err.println("Idioma inválido: " + text + ". Usando padrão: Português.");
        return PORTUGUES; // Ou lançar new IllegalArgumentException("Idioma inválido: " + text);
    }
}
