package com.dersaun.apicontas.services;

import com.dersaun.apicontas.contracts.UserCredentialDetails;
import com.dersaun.apicontas.payload.response.UsuarioResponse;
import com.dersaun.apicontas.security.AppUserDetails;
import com.dersaun.apicontas.security.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.dersaun.apicontas.security.Constants.ACCESS_TOKEN_VALIDITY_SECONDS;
import static com.dersaun.apicontas.security.Constants.SIGNING_KEY;

@Service
@SuppressWarnings("unchecked")
public class JwtTokenService {

    public String generateToken(UserCredentialDetails usuario) {

        final UsuarioResponse usuarioResponse = new UsuarioResponse(usuario);
        return Jwts.builder()
                .setSubject(usuario.getConta())
                .claim("usuario", usuarioResponse)
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS*1000))
                .compact();
    }

    public String generateToken(AppUserDetails userDetails) {
        UserCredentialDetails usuario = userDetails.getUsuario();
        try {
              return generateToken(usuario);
        }catch (Exception e ) {
            e.printStackTrace();
            throw new UsernameNotFoundException("Acesso negado");
        }
    }

    /**
     * Recuperar conta do usuário a partir do token
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getSubject);
        } catch (Exception e) {
            return null;
        }
    }

    private  <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public User getUsuario(String token) {
        Map<String, Object> userClaim = getUserFromToken(token);
        var user = new User();

        user.setId(((Integer) userClaim.get("id")).longValue());
        user.setConta((String) userClaim.get("conta"));
        user.setSenha((String) userClaim.get("senha"));
        user.setSituacao((Integer) userClaim.get("situacao"));
        user.setPessoa((Map) userClaim.get("pessoa"));
        user.setRoles((List<Map<String, String>>) userClaim.get("roles"));

        return user;
    }

    private Map<String, Object> getUserFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return (Map<String, Object>) claims.get("usuario");
    }
}
