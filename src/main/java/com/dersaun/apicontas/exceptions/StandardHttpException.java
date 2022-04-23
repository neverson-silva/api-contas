package com.dersaun.apicontas.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class StandardHttpException extends RuntimeException{

    private Long timestamp;
    private HttpStatus status;
    private String error;
    private String message;
    private Boolean show = true;
    private String path;

    public StandardHttpException(Long timestamp, Integer statusCode, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = HttpStatus.resolve(statusCode);
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public StandardHttpException(String message, Long timestamp, Integer statusCode, String error,  String path) {
        super(message);
        this.timestamp = timestamp;
        this.status =  HttpStatus.resolve(statusCode);
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public StandardHttpException(String message, Throwable cause, Long timestamp, Integer statusCode, String error, String path) {
        super(message, cause);
        this.timestamp = timestamp;
        this.status = HttpStatus.resolve(statusCode);
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public StandardHttpException(String message, String error, Integer statusCode) {
        super(message);
        this.error = error;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        setStatus(statusCode);
    }

    public StandardHttpException(String message, String error, Integer statusCode, Boolean show) {
        super(message);
        this.error = error;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.show = show;
        setStatus(statusCode);
    }

    public void setStatus(Integer code) {
        this.status = HttpStatus.resolve(code);
    }

    public Integer getStatusCode() {
        return status.value();
    }

    public String getFormattedDate() {
        Timestamp ts=new Timestamp(System.currentTimeMillis());
        Date date=new Date(ts.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        return sdf.format(date);
    }

    public static StandardHttpException notFound(String message) {
        return new StandardHttpException(message, "Recurso não encontrado", 404);
    }

    public static StandardHttpException notFound(String error, String message) {
        return new StandardHttpException(message, error, 404);
    }

    public static StandardHttpException badRequest(String message) {
        return new StandardHttpException(message, "Bad Request", 400);
    }

    public static StandardHttpException badRequest(String error, String message) {
        return new StandardHttpException(message, error, 400);
    }

    public static StandardHttpException badRequest(String error, String message, Boolean show) {
        return new StandardHttpException(message, error, 400, show);
    }

    public static StandardHttpException internalError(String message) {
        return new StandardHttpException(message, "Erro inesperado", 500);
    }

    public static StandardHttpException internalError(String message, Boolean show) {
        return new StandardHttpException(message, "Erro inesperado", 500, show);
    }

    public static StandardHttpException forbidden(String message) {
        return new StandardHttpException(message, "Acesso negado", 403);
    }

    public static StandardHttpException unauthorized(String message) {
        return new StandardHttpException(message, "Usuário não autenticado", 401);
    }

    public static StandardHttpException unprocessableEntity(String message) {
        return new StandardHttpException(message, "Unprocessable Entity", 422);
    }

    public static StandardHttpException unprocessableEntity(String message, Boolean show) {
        return new StandardHttpException(message, "Unprocessable Entity", 422, show);
    }
}