package com.dersaun.apicontas.security;

import com.dersaun.apicontas.contracts.UserCredentialDetails;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AppUserDetails implements UserDetails {

    @Getter
    private UserCredentialDetails usuario;

    private Collection<? extends GrantedAuthority> authorities;

    public AppUserDetails(UserCredentialDetails usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (authorities == null) {
          authorities = usuario.getRoles();
        }
        return authorities;
    }


    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getConta();
    }

    @Override
    public boolean isAccountNonExpired() {
        return usuario.getSituacao() != 2;
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.getSituacao() != 3;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return usuario.getSituacao() != 4;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getSituacao() == 1;
    }

//    public boolean hasAnyRole(String ...params) {
//        return UsuarioService.hasAnyRole(getAuthorities(), null, params);
//    }
//
//    public boolean hasRole(String role) {
//        return UsuarioService.hasRole(getAuthorities(), null, role);
//    }
//
//    public boolean anyNotGranted(String ...params) {
//        return !hasAnyRole(params);
//    }
//
//    public boolean notGranted(String param) {
//        return !hasRole(param);
//    }
//
//    public Boolean isAdmin() {
//        return hasRole("ROLE_ADMIN");
//    }

}
