package io.github.viniciuskegler.exception;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ControllerException extends RuntimeException  {

    private String errorCode;
    private String errorMessage;

    public ControllerException() {
    }
    public ControllerException(String errorMessage, String errorCode) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
