package com.ambergarden.orderprocessor.exception;

/**
 * Exception to indicate that user requested order does not exist
 * This exception will be handled by ApplicationExceptionHandler
 * and converts to a 403 response
 */
public class OrderNotFoundException extends OrderProcessorException {
   private static final long serialVersionUID = 1L;

   public OrderNotFoundException() {
      super("", null);
   }

   public OrderNotFoundException(String message, Throwable cause) {
      super(message, cause);
   }
}