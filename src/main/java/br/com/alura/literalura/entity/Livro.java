package br.com.alura.literalura.entity;

import br.com.alura.literalura.model.DadosLivros;
import br.com.alura.literalura.model.Idioma;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Entity
@Table(name = "livros")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;
    private Integer numeroDownloads;

    @Enumerated(EnumType.STRING)
    private Idioma idioma;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    public Livro() {}

    public Livro(DadosLivros dadosLivros) {
        this.titulo = dadosLivros.titulo();
        if (dadosLivros.idiomas() != null && !dadosLivros.idiomas().isEmpty()) {
            // Usa o seu método fromCodigoGutendex que já retorna PORTUGUES como padrão se não encontrar
            this.idioma = Idioma.fromCodigoGutendex(dadosLivros.idiomas().get(0));
        } else {
            // Se a lista de idiomas for vazia, pode definir um padrão ou null
            this.idioma = Idioma.PORTUGUES; // Ou Idioma.fromCodigoGutendex("und") se você adicionar "und" ao enum, ou null
        }
        this.numeroDownloads = dadosLivros.numeroDownloads();
    }


    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroDownloads() {
        return numeroDownloads;
    }

    public void setNumeroDownloads(Integer numeroDownloads) {
        this.numeroDownloads = numeroDownloads;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return String.format(
                "Título: %s\nAutor: %s\nIdioma: %s\nNúmero de Downloads: %d\n",
                titulo,
                (autor != null ? autor.getNome() : "Desconhecido"),
                idioma != null ? idioma.getNomeCompleto() : "N/D",
                numeroDownloads != null ? numeroDownloads : 0
        );
    }
}