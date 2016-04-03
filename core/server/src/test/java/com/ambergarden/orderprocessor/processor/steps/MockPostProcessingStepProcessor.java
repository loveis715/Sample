package com.ambergarden.orderprocessor.processor.steps;

import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;

public class MockPostProcessingStepProcessor extends PostProcessingStepProcessor {

   public MockPostProcessingStepProcessor(int orderId, OrderRepository orderRepository) {
      super(orderId, orderRepository);
   }

   @Override
   protected boolean performActions() {
      return MockStepConfig.postProcessingStepSuccess;
   }

   @Override
   protected void performRollback() {
   }
}