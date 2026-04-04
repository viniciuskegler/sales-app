package com.viniciuskegler.salesapp.shared.exception;

import java.io.Serial;

public class RecordNotFoundException  extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RecordNotFoundException(Long id) {
        super("Register not found with id: " + id);
    }
}
