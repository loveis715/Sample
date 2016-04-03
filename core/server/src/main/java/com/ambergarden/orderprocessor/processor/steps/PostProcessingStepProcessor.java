package com.ambergarden.orderprocessor.processor.steps;

import java.util.Date;

import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStep;
import com.ambergarden.orderprocessor.orm.entity.order.StepStatus;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;

/**
 * Class for processing order's post-processing step
 */
public class PostProcessingStepProcessor extends AbstractStepProcessor {

   /**
    * Constructor of PostProcessingStepProcessor
    * @param orderId the id of the order
    * @param orderRepository the order repository
    */
   public PostProcessingStepProcessor(int orderId, OrderRepository orderRepository) {
      super(orderId, orderRepository);
   }

   @Override
   public StepStatus process() {
      // Change post-processing step's state to IN_PROGRESS. Update the
      // start time of post-processing step and last update time of the order
      Date timestamp = new Date();
      Order order = orderRepository.findOne(orderId);
      OrderStep orderStep = order.getPostProcessingStep();
      orderStep.setStepStatus(StepStatus.IN_PROGRESS);
      orderStep.setStartTime(timestamp);
      orderStep.setLastUpdateTime(timestamp);
      order.setLastUpdateTime(timestamp);
      orderRepository.save(order);

      if (!performActions()) {
         return StepStatus.ROLLING_BACK;
      }

      // Step process successfully. Change the post-processing step
      // to COMPLETE and update the last update time for post-processing
      // step and order
      timestamp = new Date();
      order = orderRepository.findOne(orderId);
      orderStep = order.getPostProcessingStep();
      orderStep.setStepStatus(StepStatus.COMPLETE);
      orderStep.setLastUpdateTime(timestamp);
      order.setLastUpdateTime(timestamp);
      orderRepository.save(order);
      return StepStatus.COMPLETE;
   }

   @Override
   public StepStatus rollback() {
      // Change post-processing step's status to ROLLING_BACK and update
      // the last update for both post-processing step and order
      Date timestamp = new Date();
      Order order = orderRepository.findOne(orderId);
      OrderStep orderStep = order.getPostProcessingStep();
      orderStep.setStepStatus(StepStatus.ROLLING_BACK);
      orderStep.setLastUpdateTime(timestamp);
      order.setLastUpdateTime(timestamp);
      orderRepository.save(order);

      performRollback();

      // Change post-processing step's status to ROLLBACKED and update
      // the last update for both post-processing step and order
      timestamp = new Date();
      order = orderRepository.findOne(orderId);
      orderStep = order.getPostProcessingStep();
      orderStep.setStepStatus(StepStatus.ROLLBACKED);
      orderStep.setLastUpdateTime(timestamp);
      order.setLastUpdateTime(timestamp);
      orderRepository.save(order);
      return StepStatus.ROLLBACKED;
   }
}