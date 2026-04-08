package br.anhembi.groove_aplication.exception;

public class UserNotFoundException extends RuntimeException {
     public UserNotFoundException(String cpf) {
          super("User with CPF " + cpf + " not found");
     }

     public UserNotFoundException(String message, Throwable cause) {
          super(message, cause);
     }
}
