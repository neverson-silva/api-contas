package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.models.Usuario;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select u \n" +
            "from Usuario u \n" +
            "inner join fetch u.pessoa pessoa \n" +
            "inner join fetch u.regras \n" +
            "where u.conta = :conta \n")
    Optional<Usuario> findByConta(@Param("conta") String conta);

    @Query("        select u\n " +
            "        from Usuario u \n " +
            "        inner join fetch u.pessoa pessoa \n" +
            "        inner join fetch u.regras regras \n" +
            "        order by u.conta ")
    FastList<Usuario> findAllOrderByConta();

    @Query("select u \n" +
            "from Usuario u \n" +
            "inner join fetch u.pessoa pessoa \n" +
            "inner join fetch u.regras regras \n" +
            "where u.id = ?1")
    Optional<Usuario> findById(Integer id);

    Optional<Usuario> findByPessoa(Pessoa pessoa);
}
