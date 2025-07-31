// src/main/java/br/com/alura/literalura/LiterAluraApplication.java
package br.com.alura.literalura; // PACOTE EXATO DA SUA IMAGEM

import br.com.alura.literalura.principal.Principal; // Importa a classe Principal

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.com.alura.literalura.repository") // Onde estão os repositórios
public class LiteraluraApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	// Bean para que Principal seja executada como um CommandLineRunner
	@Bean
	public CommandLineRunner run(Principal principal) { // Injete a classe Principal
		return args -> principal.exibeMenu();
	}
}