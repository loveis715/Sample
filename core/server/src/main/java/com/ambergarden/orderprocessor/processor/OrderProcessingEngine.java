package com.ambergarden.orderprocessor.processor;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.ambergarden.orderprocessor.Constants;
import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStep;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;

/**
 * Component used to pick orders for processing.
 * It will periodically(5 seconds) check whether we have orders to process.
 * If so, use the task executor to execute the order processing logics
 */
@Component
public class OrderProcessingEngine {
   @Autowired
   private OrderRepository orderRepository;

   @Autowired
   @Qualifier(value = "orderProcessor")
   private TaskExecutor taskExecutor;

   public void process() {
      // Process all orders assigned to this node
      List<Order> orders = orderRepository.findAllByProcessingNode(Constants.PROCESSING_NODE_NAME);
      for (Order order : orders) {
         // If we have already start processing, continue
         if (isUnderProcessing(order)) {
            continue;
         }

         // Create a new task and execute it in taskExecutor's thread pool
         Runnable executor = new OrderProcessingTask(order.getId(), orderRepository);
         taskExecutor.execute(executor);
      }
   }

   // If the last update time recorded in each step before the time recorded by order,
   // it should be a new order just assigned to this node, or an order re-scheduled to
   // this node
   // Every update in order processing engine should update both the order and order
   // step at the same time
   private boolean isUnderProcessing(Order order) {
      OrderStep lastUpdatedStep = findLastUpdatedStep(order);
      if (lastUpdatedStep == null) {
         // No step have a last update time. That means no step has started process
         return false;
      }

      // Some steps has been processed, but later than the order's last update time
      // That means a re-dispatch has happened
      Date lastUpdateTime = lastUpdatedStep.getLastUpdateTime();
      return lastUpdateTime.before(order.getLastUpdateTime());
   }

   // Find the last updated order step, by finding the latest lastUpdateTime
   // in each order step
   private OrderStep findLastUpdatedStep(Order order) {
      OrderStep lastUpdatedStep = null;
      Date latestTime = null;
      // Scheduling step should go first, since the order may not
      // have any chance to execute
      OrderStep orderStep = order.getSchedulingStep();
      if (orderStep.getLastUpdateTime() != null) {
         lastUpdatedStep = orderStep;
         latestTime = orderStep.getLastUpdateTime();
      }

      orderStep = order.getPreprocessingStep();
      if (orderStep.getLastUpdateTime() != null
         && (latestTime == null || orderStep.getLastUpdateTime().after(latestTime))) {
         lastUpdatedStep = orderStep;
         latestTime = orderStep.getLastUpdateTime();
      }

      orderStep = order.getProcessingStep();
      if (orderStep.getLastUpdateTime() != null
         && (latestTime == null || orderStep.getLastUpdateTime().after(latestTime))) {
         lastUpdatedStep = orderStep;
         latestTime = orderStep.getLastUpdateTime();
      }

      orderStep = order.getPostProcessingStep();
      if (orderStep.getLastUpdateTime() != null
         && (latestTime == null || orderStep.getLastUpdateTime().after(latestTime))) {
         lastUpdatedStep = orderStep;
         latestTime = orderStep.getLastUpdateTime();
      }

      return lastUpdatedStep;
   }
}