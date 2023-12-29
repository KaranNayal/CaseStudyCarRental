package com.crs.exception;

/**
 * Exception class indicating that a customer with the same details already exists.
 */
public class CustomerAlreadyExistsException extends Exception {

    /**
     * Constructs a new CustomerAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
}
