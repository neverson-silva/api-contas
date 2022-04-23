package com.dersaun.apicontas.dao.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "devedores")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@ToString()
public class Pessoa implements Comparable<Pessoa>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "devedor_id")
    private Long id;

    @Column(length = 50, nullable = false)
    @NonNull
    private String nome;

    @Column(length = 50, nullable = false)
    @NonNull
    private String sobrenome;

    private String apelido;

    @Column(name = "profile")
    private String perfil;

    @CreationTimestamp
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Column(name = "sexo")
    @NonNull
    private String sexo;

    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dataNascimento;

//    @OneToOne(mappedBy = "pessoa", fetch = FetchType.LAZY)
//    @JsonIgnore
//    private Usuario usuario;

    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }

    public Pessoa(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(@NotNull Pessoa o) {
        String fullName = this.getNome();
        String otherFullName = o.getNome();
        return fullName.compareTo(otherFullName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pessoa pessoa = (Pessoa) o;
        return Objects.equals(id, pessoa.id) && nome.equals(pessoa.nome) && sobrenome.equals(pessoa.sobrenome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, sobrenome, apelido, perfil, createdAt, updatedAt, sexo, dataNascimento);
    }
}