package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/** Erro de negócio com o status HTTP que deve voltar para a tela (404, 409, 401...). */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
