package com.ambergarden.orderprocessor.processor.steps;

import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;

public class MockSchedulingStepProcessor extends SchedulingStepProcessor {

   public MockSchedulingStepProcessor(int orderId, OrderRepository orderRepository) {
      super(orderId, orderRepository);
   }

   @Override
   protected boolean performActions() {
      return MockStepConfig.schedulingStepSuccess;
   }

   @Override
   protected void performRollback() {
   }
}