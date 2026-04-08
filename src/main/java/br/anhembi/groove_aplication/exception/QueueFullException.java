package br.anhembi.groove_aplication.exception;

public class QueueFullException extends RuntimeException {
     public QueueFullException(String dia) {
          super("Queue for day " + dia + " is full");
     }

     public QueueFullException(String message, Throwable cause) {
          super(message, cause);
     }
}
