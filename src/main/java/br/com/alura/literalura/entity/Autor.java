// src/main/java/br/com/alura/literalura/entity/Autor.java
package br.com.alura.literalura.entity; // PACOTE EXATO DA SUA IMAGEM

import br.com.alura.literalura.model.DadosAutores; // Importa a record de modelo
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    private Integer anoNascimento;
    private Integer anoFalecimento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Livro> livros = new ArrayList<>();

    public Autor() {}

    public Autor(DadosAutores dadosAutor) {
        this.nome = dadosAutor.nome();
        this.anoNascimento = dadosAutor.anoNascimento();
        this.anoFalecimento = dadosAutor.anoFalecimento();
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getAnoNascimento() { return anoNascimento; }
    public void setAnoNascimento(Integer anoNascimento) { this.anoNascimento = anoNascimento; }
    public Integer getAnoFalecimento() { return anoFalecimento; }
    public void setAnoFalecimento(Integer anoFalecimento) { this.anoFalecimento = anoFalecimento; }
    public List<Livro> getLivros() { return livros; }
    public void setLivros(List<Livro> livros) { this.livros = livros; }

    // --- Métodos de Conveniência ---
    public boolean estaVivoEm(int ano) {
        return (anoNascimento != null && anoNascimento <= ano) &&
                (anoFalecimento == null || anoFalecimento > ano);
    }

    public void adicionarLivro(Livro livro) {
        this.livros.add(livro);
        livro.setAutor(this);
    }

    @Override
    public String toString() {
        return "Autor: " + nome +
                ", Nascimento: " + (anoNascimento != null ? anoNascimento : "N/D") +
                ", Falecimento: " + (anoFalecimento != null ? anoFalecimento : "Vivo") +
                ", Livros: " + (livros.isEmpty() ? "Nenhum" : livros.stream().map(Livro::getTitulo).collect(Collectors.joining(", ")));
    }

    // --- equals() e hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Autor autor = (Autor) o;
        return Objects.equals(nome, autor.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}