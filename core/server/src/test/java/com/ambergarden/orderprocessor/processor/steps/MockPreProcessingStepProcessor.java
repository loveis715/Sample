package com.ambergarden.orderprocessor.processor.steps;

import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;

public class MockPreProcessingStepProcessor extends PreProcessingStepProcessor {

   public MockPreProcessingStepProcessor(int orderId, OrderRepository orderRepository) {
      super(orderId, orderRepository);
   }

   @Override
   protected boolean performActions() {
      return MockStepConfig.preProcessingStepSuccess;
   }

   @Override
   protected void performRollback() {
   }
}