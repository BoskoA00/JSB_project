package com.example.demo.Exceptions;

public class AnswerNotFound extends RuntimeException {
    public AnswerNotFound(String message) {
        super(message);
    }
}
