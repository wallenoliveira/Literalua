// src/main/java/br/com/alura/literalura/service/IConverteDados.java
package br.com.alura.literalura.service; // PACOTE EXATO DA SUA IMAGEM

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);
}