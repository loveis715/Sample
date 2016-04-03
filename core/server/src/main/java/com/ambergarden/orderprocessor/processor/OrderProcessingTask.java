package com.ambergarden.orderprocessor.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.ambergarden.orderprocessor.HomeController;
import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;
import com.ambergarden.orderprocessor.orm.entity.order.StepStatus;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;
import com.ambergarden.orderprocessor.processor.ProcessingContext.ProcessAction;
import com.ambergarden.orderprocessor.processor.steps.StepProcessor;

/**
 * Task used to process orders
 */
public class OrderProcessingTask implements Runnable {
   private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

   private final int orderId;
   private final OrderRepository orderRepository;

   /**
    * Constructor for OrderProcessingTask
    * @param orderId the id of the order for processing
    * @param orderRepository order's repository
    */
   public OrderProcessingTask(int orderId, OrderRepository orderRepository) {
      this.orderId = orderId;
      this.orderRepository = orderRepository;
   }

   @Override
   public void run() {
      try {
         // Use the processing context to build the processing step chain
         // and restore to previous processing state
         ProcessingContext context = createProcessingContext(orderId, orderRepository);
         context.buildStepChain();
         context.restorePreviousProcessingStatus();

         // Start process
         processOrder(context);
      } catch (ObjectOptimisticLockingFailureException ex) {
         // This is a rare case. It will only happen when we just want to
         // process that order, dispatcher performs a re-dispatch.
      } catch (RuntimeException ex) {
         logger.error("Unhandled exception during processing order " + orderId, ex);
      }
   }

   protected ProcessingContext createProcessingContext(int orderId, OrderRepository orderRepository) {
      return new ProcessingContext(orderId, orderRepository);
   }

   private void processOrder(ProcessingContext context) {
      logger.info("Start processing order " + orderId);

      // Process the order
      ProcessAction processAction = context.getProcessAction();
      StepProcessor processingStep = context.getStartingStep();
      while (processingStep != null) {
         if (processAction == ProcessAction.PROCESS) {
            StepStatus status = processingStep.process();
            if (status == StepStatus.ROLLING_BACK) {
               // This step fails, we need to start rollback from current step
               processAction = ProcessAction.ROLLBACK;

               Order order = orderRepository.findOne(orderId);
               order.setOrderStatus(OrderStatus.ROLLING_BACK);
               orderRepository.save(order);
            } else {
               // Complete successfully. Continue to process next step
               processingStep = processingStep.getNext();
               if (processingStep == null) {
                  // We have complete all steps. Mark order as complete
                  Order order = orderRepository.findOne(orderId);
                  order.setOrderStatus(OrderStatus.COMPLETE);
                  order.setProcessingNode(null);
                  orderRepository.save(order);
               }
            }
         } else {
            processingStep.rollback();
            processingStep = processingStep.getPrev();
            if (processingStep == null) {
               // We have rollbacked all steps. Mark order as failed
               Order order = orderRepository.findOne(orderId);
               order.setOrderStatus(OrderStatus.FAILED);
               order.setProcessingNode(null);
               orderRepository.save(order);
            }
         }
      }

      logger.info("Processing order " + orderId + " complete");
   }
}