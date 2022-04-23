package com.dersaun.apicontas.dao.models;

import com.dersaun.apicontas.services.UsuarioService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cartoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormaPagamento implements Comparable<FormaPagamento>{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "cartao_id")
    private Long id;

    @Column(length = 15)
    private String nome;

    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devedor_id")
    @JsonIgnore
    private Pessoa dono;

    private Integer vencimento;

    private String cor;

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

    public Boolean isAtivo() {
        return ativo;
    }

    public FormaPagamento(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(@NotNull FormaPagamento o) {
        return this.getNome().compareTo(o.getNome());
    }

    public Boolean isReadOnly() {
//        return dono == null && UsuarioService.usuario().isAdmin();
        return dono == null && !UsuarioService.usuario().isAdmin();
    }

    public Long getDonoId() {
        return dono != null ? dono.getId() : null;
    }
}
