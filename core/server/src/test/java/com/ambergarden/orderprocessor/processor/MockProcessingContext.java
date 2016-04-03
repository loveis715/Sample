package com.ambergarden.orderprocessor.processor;

import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;
import com.ambergarden.orderprocessor.processor.steps.MockPostProcessingStepProcessor;
import com.ambergarden.orderprocessor.processor.steps.MockPreProcessingStepProcessor;
import com.ambergarden.orderprocessor.processor.steps.MockProcessingStepProcessor;
import com.ambergarden.orderprocessor.processor.steps.MockSchedulingStepProcessor;
import com.ambergarden.orderprocessor.processor.steps.StepProcessor;

public class MockProcessingContext extends ProcessingContext {
   private final int orderId;
   private final OrderRepository orderRepository;

   public MockProcessingContext(int orderId, OrderRepository orderRepository) {
      super(orderId, orderRepository);

      this.orderId = orderId;
      this.orderRepository = orderRepository;
   }

   @Override
   public StepProcessor buildStepChain() {
      schedulingStep = new MockSchedulingStepProcessor(orderId, orderRepository);
      preProcessingStep = new MockPreProcessingStepProcessor(orderId, orderRepository);
      processingStep = new MockProcessingStepProcessor(orderId, orderRepository);
      postProcessingStep = new MockPostProcessingStepProcessor(orderId, orderRepository);

      schedulingStep.setNext(preProcessingStep);
      preProcessingStep.setNext(processingStep);
      processingStep.setNext(postProcessingStep);

      postProcessingStep.setPrev(processingStep);
      processingStep.setPrev(preProcessingStep);
      preProcessingStep.setPrev(schedulingStep);

      this.startingStep = schedulingStep;

      return schedulingStep;
   }
}