package br.com.alura.literalura.repository;

import br.com.alura.literalura.entity.Livro;
import br.com.alura.literalura.model.Idioma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Manter Optional para outros findById etc.

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    // CORREÇÃO: Mudei para List<Livro> para evitar NonUniqueResultException
    List<Livro> findByTituloContainsIgnoreCase(String titulo);

    // Se você tiver este método com @Column(unique=true) no titulo,
    // Optional<Livro> findByTituloIgnoreCase(String titulo);
    // Mas o seu stack trace indicou findByTituloContainsIgnoreCase

    List<Livro> findByIdioma(Idioma idioma);

    List<Livro> findTop10ByOrderByNumeroDownloadsDesc();
}