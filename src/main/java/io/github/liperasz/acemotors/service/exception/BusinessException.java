package io.github.liperasz.acemotors.service.exception;

import org.springframework.http.HttpStatus;

// Exceção de regra de negócio.
// messageKey é a chave da mensagem para o usuário no messages.properties
public class BusinessException extends RuntimeException {

    private final String messageKey;

    private final HttpStatus status;

    public BusinessException (String messageKey, String developerMessage) {
        this(messageKey, developerMessage, HttpStatus.BAD_REQUEST);
    }

    public BusinessException (String messageKey, String developerMessage, HttpStatus status) {
        super(developerMessage);
        this.messageKey = messageKey;
        this.status = status;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
