package com.ambergarden.orderprocessor.exception;

/**
 * Exception to indicate that user requested order does not exist
 * This exception will be handled by ApplicationExceptionHandler
 * and converts to a 403 response
 */
public class EntityNotFoundException extends OrderProcessorException {
   private static final long serialVersionUID = 1L;

   public EntityNotFoundException() {
      super("", null);
   }

   public EntityNotFoundException(String message, Throwable cause) {
      super(message, cause);
   }
}