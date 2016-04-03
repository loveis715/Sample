package com.ambergarden.orderprocessor.processor;

import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;
import com.ambergarden.orderprocessor.orm.entity.order.StepStatus;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;
import com.ambergarden.orderprocessor.processor.steps.PostProcessingStepProcessor;
import com.ambergarden.orderprocessor.processor.steps.PreProcessingStepProcessor;
import com.ambergarden.orderprocessor.processor.steps.ProcessingStepProcessor;
import com.ambergarden.orderprocessor.processor.steps.SchedulingStepProcessor;
import com.ambergarden.orderprocessor.processor.steps.StepProcessor;

/**
 * Class used to hold the context for order processing
 */
public class ProcessingContext {
   private final int orderId;
   private final OrderRepository orderRepository;

   private ProcessAction processAction = ProcessAction.PROCESS;
   protected SchedulingStepProcessor schedulingStep;
   protected PreProcessingStepProcessor preProcessingStep;
   protected ProcessingStepProcessor processingStep;
   protected PostProcessingStepProcessor postProcessingStep;

   protected StepProcessor startingStep;

   /**
    * Constructor for ProcessingContext
    * @param orderId the order's id
    * @param orderRepository order repository
    */
   public ProcessingContext(int orderId, OrderRepository orderRepository) {
      this.orderId = orderId;
      this.orderRepository = orderRepository;
   }

   /**
    * Get the action we need to take for that order
    * @return the processing action
    */
   public ProcessAction getProcessAction() {
      return this.processAction;
   }

   /**
    * Get the step we need to start at
    * @return the step we need to start at
    */
   public StepProcessor getStartingStep() {
      return this.startingStep;
   }

   /**
    * Build a chain of double-linked processing steps.
    * @return the head of that chain
    */
   public StepProcessor buildStepChain() {
      schedulingStep = new SchedulingStepProcessor(orderId, orderRepository);
      preProcessingStep = new PreProcessingStepProcessor(orderId, orderRepository);
      processingStep = new ProcessingStepProcessor(orderId, orderRepository);
      postProcessingStep = new PostProcessingStepProcessor(orderId, orderRepository);

      schedulingStep.setNext(preProcessingStep);
      preProcessingStep.setNext(processingStep);
      processingStep.setNext(postProcessingStep);

      postProcessingStep.setPrev(processingStep);
      processingStep.setPrev(preProcessingStep);
      preProcessingStep.setPrev(schedulingStep);

      this.startingStep = schedulingStep;

      return schedulingStep;
   }

   /**
    * If our system has already processed that order, but failed to complete it,
    * we need to restore to previous processing state and continue
    */
   public void restorePreviousProcessingStatus() {
      Order order = orderRepository.findOne(orderId);
      if (order.getOrderStatus() == OrderStatus.IN_PROGRESS) {
         processAction = ProcessAction.PROCESS;

         // Check steps backwards, to find the last one which is not complete
         // Use this variable currentStep to record the temporary calculation result
         StepProcessor currentStep = null;
         if (order.getPostProcessingStep().getStepStatus() != StepStatus.COMPLETE) {
            currentStep = postProcessingStep;
         }
         if (order.getProcessingStep().getStepStatus() != StepStatus.COMPLETE) {
            currentStep = processingStep;
         }
         if (order.getPreprocessingStep().getStepStatus() != StepStatus.COMPLETE) {
            currentStep = preProcessingStep;
         }
         if (order.getSchedulingStep().getStepStatus() != StepStatus.COMPLETE) {
            currentStep = schedulingStep;
         }
         this.startingStep = currentStep;
      } else {
         processAction = ProcessAction.ROLLBACK;

         // Check steps backwards, to find the last one which is not complete
         // Use this variable currentStep to record the temporary calculation result
         StepProcessor currentStep = null;
         if (order.getSchedulingStep().getStepStatus() != StepStatus.ROLLBACKED) {
            currentStep = schedulingStep;
         }
         if (order.getPreprocessingStep().getStepStatus() != StepStatus.ROLLBACKED) {
            currentStep = preProcessingStep;
         }
         if (order.getProcessingStep().getStepStatus() != StepStatus.ROLLBACKED) {
            currentStep = processingStep;
         }
         if (order.getPostProcessingStep().getStepStatus() != StepStatus.ROLLBACKED) {
            currentStep = postProcessingStep;
         }
         this.startingStep = currentStep;
      }
   }

   // An inner enumeration to indicate the current action we should take
   // Enum is implicitly static, so no need a static keyword
   public enum ProcessAction {
      PROCESS,
      ROLLBACK
   }
}