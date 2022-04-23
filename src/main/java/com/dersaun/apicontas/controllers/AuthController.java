package com.dersaun.apicontas.controllers;

import com.dersaun.apicontas.dao.models.RefreshToken;
import com.dersaun.apicontas.exceptions.TokenRefreshException;
import com.dersaun.apicontas.payload.request.TokenRefreshRequest;
import com.dersaun.apicontas.payload.request.UserCreateRequest;
import com.dersaun.apicontas.payload.request.UserCredentials;
import com.dersaun.apicontas.payload.response.TokenRefreshResponse;
import com.dersaun.apicontas.payload.response.UsuarioResponse;
import com.dersaun.apicontas.security.AppUserDetails;
import com.dersaun.apicontas.services.JwtTokenService;
import com.dersaun.apicontas.services.RefreshTokenService;
import com.dersaun.apicontas.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/auth")
@RestController
public class AuthController {

    @Autowired
    private JwtTokenService tokenService;

    @Autowired()
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserCredentials credentials) {

        Authentication authentication  = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getConta(), credentials.getSenha()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

        String token = tokenService.generateToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.getRefreshToken(userDetails.getUsuario().getId());

        UsuarioResponse usuarioResponse = new UsuarioResponse(userDetails.getUsuario(), token, refreshToken.getToken() );

        return ResponseEntity.ok(usuarioResponse);

    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestBody() UserCreateRequest user) {

        usuarioService.salvar(user);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken( @Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {

        String requestRefreshToken = tokenRefreshRequest.getRefreshToken();

        var refreshToken =  refreshTokenService.findByToken(requestRefreshToken);

        return refreshToken
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUsuario)
                .map(user -> {
                    String token = tokenService.generateToken(user);
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token não encontrado na base de dados!"));
    }
}
