package com.ambergarden.orderprocessor.processor.steps;

import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;

public class MockProcessingStepProcessor extends ProcessingStepProcessor {

   public MockProcessingStepProcessor(int orderId, OrderRepository orderRepository) {
      super(orderId, orderRepository);
   }

   @Override
   protected boolean performActions() {
      return MockStepConfig.processingStepSuccess;
   }

   @Override
   protected void performRollback() {
   }
}