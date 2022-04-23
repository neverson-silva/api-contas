package com.dersaun.apicontas.services;

import com.dersaun.apicontas.contracts.UserCredentialDetails;
import com.dersaun.apicontas.dao.models.Pessoa;
import com.dersaun.apicontas.dao.models.Usuario;
import com.dersaun.apicontas.exceptions.StandardHttpException;
import com.dersaun.apicontas.payload.request.UserCreateRequest;
import com.dersaun.apicontas.security.AppUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public interface UsuarioService extends Service<Usuario, Long>{

    void salvar(UserCreateRequest user);

    static UserCredentialDetails usuario() {

        var userDetails = authenticated();

        return userDetails.getUsuario() ;
    }

    static AppUserDetails authenticated() {

        try {
            AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            if (userDetails != null) {
                return userDetails;
            }
        } catch (Exception e) {
        }

        throw new StandardHttpException("Acesso negado", "Usuário não autenticado ou sem permissão de acesso ao recurso", 401);
    }

    static boolean hasAnyRole(Collection<? extends GrantedAuthority> authorities, String... params) {
         return Arrays.stream(params)
                    .anyMatch(param -> authorities.contains(new SimpleGrantedAuthority(param)));
    }

    static boolean hasRole(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities.contains(new SimpleGrantedAuthority(role));
    }

    Optional<Usuario> findByPessoa(Pessoa pessoa);
}
