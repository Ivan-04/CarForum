package com.example.carforum.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found.", type, attribute, value));
    }

    public EntityNotFoundException(String type, int id) {
        super(String.format("%s with ID %d not found.", type, id));
    }

}
