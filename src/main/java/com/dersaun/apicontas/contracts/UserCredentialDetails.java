package com.dersaun.apicontas.contracts;

import com.dersaun.apicontas.dao.models.Pessoa;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * UserCredentialDetails to be used in Authentication workflow
 */
public interface UserCredentialDetails {
    Long getId();

    String getSenha();

    String getConta();

    Integer getSituacao();

    Pessoa getPessoa();

    Collection<? extends GrantedAuthority> getRoles();

    boolean hasAnyRole(String... params);

    boolean hasRole(String role);

    boolean anyNotGranted(String... params);

    boolean notGranted(String param);

    Boolean isAdmin();
}
