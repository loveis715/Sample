package com.ambergarden.orderprocessor.exception;

/**
 * Base exception for <code>ApplicationExceptionHandler</code> to handle.
 * When derived from this class, exception thrown from order processor
 * service will be handled by ApplicationExceptionHandler.
 */
public class OrderProcessorException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public OrderProcessorException(String message, Throwable cause) {
      super(message, cause);
   }
}