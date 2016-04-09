package com.ambergarden.orderprocessor.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ambergarden.orderprocessor.HomeController;
import com.ambergarden.orderprocessor.exception.BadEntityRequestException;
import com.ambergarden.orderprocessor.exception.EntityNotFoundException;
import com.ambergarden.orderprocessor.exception.OrderProcessorException;
import com.ambergarden.orderprocessor.localization.Localizer;

/**
 * The <code>ApplicationExceptionHandler</code> advice is used to handle
 * all exceptions thrown from order processor service. It will generate
 * HTTP status code and messages according to the caught exception
 * To add new exception handlers, just use @ExceptionHandler annotation
 * to indicate the exception we'd want to process, and defines the exception
 * processing logic in the annotated method.
 * Be careful that exception will be matched in defined order. That is, we
 * should put exception handler for RuntimeException at last
 */
@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
   private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

   private static Localizer localizer = Localizer.getLocalizer();

   @ExceptionHandler({
      BadEntityRequestException.class
   })
   @ResponseBody
   public ResponseEntity<?> handleBadEntityRequestException(OrderProcessorException exception, WebRequest request) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(localizer.getLocalizedText("exception.handling.BadOrderRequestException"));
   }

   @ExceptionHandler({
      EntityNotFoundException.class
   })
   @ResponseBody
   public ResponseEntity<?> handleEntityNotFoundException(OrderProcessorException exception, WebRequest request) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(localizer.getLocalizedText("exception.handling.OrderNotFoundException"));
   }

   @ExceptionHandler({
      OrderProcessorException.class
   })
   @ResponseBody
   public ResponseEntity<?> handleOrderProcessorException(OrderProcessorException exception, WebRequest request) {
      addToLog(exception);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(localizer.getLocalizedText("exception.handling.UnhandledServerSideException"));
   }

   @ExceptionHandler({
      RuntimeException.class
   })
   @ResponseBody
   public ResponseEntity<?> handleRuntimeException(OrderProcessorException exception, WebRequest request) {
      addToLog(exception);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(localizer.getLocalizedText("exception.handling.UnhandledServerSideException"));
   }

   private void addToLog(RuntimeException ex) {
      logger.error(ex.getMessage(), ex);
   }
}