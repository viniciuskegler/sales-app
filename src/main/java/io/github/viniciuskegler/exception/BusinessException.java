package io.github.viniciuskegler.exception;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class BusinessException extends RuntimeException {

    private String errorCode;
    private String errorMessage;

    public BusinessException() {
    }
    public BusinessException(String errorMessage, String errorCode) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
