// src/main/java/br/com/alura/literalura/repository/AutorRepository.java
package br.com.alura.literalura.repository;

import br.com.alura.literalura.entity.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> { // Deve ser uma INTERFACE
    Optional<Autor> findByNomeContainsIgnoreCase(String nome);
    @Query("SELECT a FROM Autor a WHERE a.anoNascimento <= :ano AND (a.anoFalecimento IS NULL OR a.anoFalecimento > :ano)")
    List<Autor> buscarAutoresVivosEmAno(Integer ano);
    List<Autor> findAllByOrderByNomeAsc();

    @Query("SELECT a FROM Autor a WHERE lower(a.nome) LIKE lower(concat('%', :nome, '%'))")
    List<Autor> findByNomeContainingIgnoreCase(String nome);
}