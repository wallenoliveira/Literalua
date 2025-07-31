// src/main/java/br/com/alura/literalura/service/ConverteDados.java
package br.com.alura.literalura.service; // PACOTE EXATO DA SUA IMAGEM

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class ConverteDados implements IConverteDados {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter JSON para " + classe.getName(), e);
        }
    }
}
