package com.dersaun.apicontas.exceptions;

public class TokenRefreshException extends StandardHttpException {

    public TokenRefreshException(String refreshToken, String message) {
        super("Falha [" + refreshToken + "]: " + message, "Acesso não autorizado" ,403);
    }
}
