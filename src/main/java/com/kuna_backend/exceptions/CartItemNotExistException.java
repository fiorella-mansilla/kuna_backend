package com.kuna_backend.exceptions;

public class CartItemNotExistException extends IllegalArgumentException {
    public CartItemNotExistException (String msg) {
        super(msg);
    }
}
