package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.Usuario;
import com.dersaun.apicontas.dao.repositories.UsuarioRepository;
import com.dersaun.apicontas.security.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Usuario usuario = usuarioRepository.findByConta(username)
                                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new AppUserDetails(usuario);
    }
}
