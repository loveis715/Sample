package com.ambergarden.orderprocessor.exception;

/**
 * Exception to indicate that user's input is not valid
 * This exception will be handled by ApplicationExceptionHandler
 * and converts to a 404 response
 */
public class BadOrderRequestException extends OrderProcessorException {
   private static final long serialVersionUID = 1L;

   public BadOrderRequestException() {
      super("", null);
   }

   public BadOrderRequestException(String message, Throwable cause) {
      super(message, cause);
   }
}