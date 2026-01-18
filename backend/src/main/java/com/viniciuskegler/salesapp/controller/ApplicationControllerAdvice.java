package com.viniciuskegler.salesapp.controller;

import com.viniciuskegler.salesapp.exception.RecordNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundException(RecordNotFoundException ex,
                                                                 HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createProblemDetail(
                HttpStatus.NOT_FOUND,
                "Record Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                null
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                               HttpServletRequest request) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .reduce("", (acc, error) -> acc + error + "\n");
        return ResponseEntity.badRequest().body(createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Argument not valid",
                errorMsg,
                request.getRequestURI(),
                null
        ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ProblemDetail> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            String name = result.getMethodParameter().getParameterName();
            result.getResolvableErrors().forEach(error -> errors
                    .put(name, error.getDefaultMessage()));
        });

        return ResponseEntity.badRequest().body(createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "One or more parameters are invalid.",
                request.getRequestURI(),
                errors
        ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException ex,
                                                                            HttpServletRequest request) {
        String errorMsg = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString() + " " + violation.getMessage())
                .collect(Collectors.joining("\n"));

        return ResponseEntity.badRequest().body(createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                errorMsg,
                request.getRequestURI(),
                null
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        String typeName = "unknown";
        if (Objects.nonNull(ex.getRequiredType())) {
            typeName = ex.getRequiredType().getSimpleName();
        }
        String errorMsg = String.format("The parameter '%s' should be of type '%s'",
                ex.getName(), typeName);
        return ResponseEntity.badRequest().body(createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Type Mismatch",
                errorMsg,
                request.getRequestURI(),
                null
        ));
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail, String uri,
                                              Map<String, String> errors) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(uri));
        if (Objects.nonNull(errors)) {
            problemDetail.setProperty("errors", errors);
        }
        return problemDetail;
    }
}
