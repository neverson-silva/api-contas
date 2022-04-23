package com.dersaun.apicontas.services.impl;

import com.dersaun.apicontas.dao.models.RefreshToken;
import com.dersaun.apicontas.dao.repositories.RefreshTokenRepository;
import com.dersaun.apicontas.dao.repositories.UsuarioRepository;
import com.dersaun.apicontas.exceptions.StandardHttpException;
import com.dersaun.apicontas.exceptions.TokenRefreshException;
import com.dersaun.apicontas.security.Constants;
import com.dersaun.apicontas.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUsuario(usuarioRepository.findById(userId).get());
        generateToken(refreshToken);
        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, noRollbackFor = StandardHttpException.class)
    public RefreshToken getRefreshToken(Long userId) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByUsuarioId(userId);
        RefreshToken refreshToken = null;
        var hoje = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();

        if (refreshTokenOpt.isEmpty()) {
            refreshToken = createRefreshToken(userId);
        } else if (hoje.isAfter(refreshTokenOpt.get().getExpiryDate())) {
            refreshToken = refresh(refreshTokenOpt.get());
        } else {
            refreshToken = refreshTokenOpt.get();
        }

        return refreshToken;
    }

    @Override
    public RefreshToken refresh(RefreshToken refreshToken) {
        generateToken(refreshToken);
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    private void generateToken(RefreshToken refreshToken) {
        refreshToken.setExpiryDate(Instant.now().plusMillis(Constants.ACCESS_REFRESH_TOKEN_VALIDITY_SECONDS * 1000));
        refreshToken.setToken(UUID.randomUUID().toString());
    }

    //    @Transactional()
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);//deleteByIdAndToken(token.getId(), token.getToken());
            throw new TokenRefreshException(token.getToken(), "Refresh token expirado. Por favor faça uma nova requisição de login.");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUsuario(usuarioRepository.findById(userId).get());
    }
}
