package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.RefreshToken;
import com.dersaun.apicontas.dao.models.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Override
    @EntityGraph(attributePaths = {"usuario", "usuario.regras", "usuario.pessoa"})
    Optional<RefreshToken> findById(Long id);

    @EntityGraph(attributePaths = {"usuario", "usuario.regras", "usuario.pessoa"})
    Optional<RefreshToken> findByToken(String token);

    @EntityGraph(attributePaths = {"usuario"})
    Optional<RefreshToken> findByUsuarioId(Long usuarioId);

    int deleteByUsuario(Usuario usuario);

}
