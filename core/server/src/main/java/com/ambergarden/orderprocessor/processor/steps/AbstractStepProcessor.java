package com.ambergarden.orderprocessor.processor.steps;

import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;

/**
 * Base class for processing steps
 */
public abstract class AbstractStepProcessor implements StepProcessor {
   private StepProcessor prevStep;
   private StepProcessor nextStep;
   protected final int orderId;
   protected final OrderRepository orderRepository;

   public AbstractStepProcessor(int orderId, OrderRepository orderRepository) {
      this.orderId = orderId;
      this.orderRepository = orderRepository;
   }

   @Override
   public StepProcessor getNext() {
      return nextStep;
   }

   public void setNext(StepProcessor nextStep) {
      this.nextStep = nextStep;
   }

   @Override
   public StepProcessor getPrev() {
      return prevStep;
   }

   public void setPrev(StepProcessor prevStep) {
      this.prevStep = prevStep;
   }

   // Fake method to simulating long run actions
   // Override it to inject fault for test
   protected boolean performActions() {
      try {
         Thread.sleep(5000);
      } catch (InterruptedException e) {
         // This is simulation, so we will not handle it
      }

      return Math.rint(Math.random()) % 20 != 0;
   }

   protected void performRollback() {
      try {
         Thread.sleep(5000);
      } catch (InterruptedException e) {
         // This is simulation, so we will not handle it
      }
   }
}