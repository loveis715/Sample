package com.ambergarden.orderprocessor.exception;

/**
 * Exception to indicate that user's input is not valid
 * This exception will be handled by ApplicationExceptionHandler
 * and converts to a 404 response
 */
public class BadEntityRequestException extends OrderProcessorException {
   private static final long serialVersionUID = 1L;

   public BadEntityRequestException() {
      super("", null);
   }

   public BadEntityRequestException(String message, Throwable cause) {
      super(message, cause);
   }
}