package com.ambergarden.orderprocessor.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.orderprocessor.TestUtils;
import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;
import com.ambergarden.orderprocessor.processor.ProcessingContext.ProcessAction;
import com.ambergarden.orderprocessor.processor.steps.StepProcessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/spring/order-test-context.xml" })
public class ProcessingContextTest {
   @Autowired
   private OrderRepository orderRepository;

   @Test
   public void testParsingNewOrder() {
      Order mockOrder = TestUtils.createMockOrder();
      mockOrder.setOrderStatus(OrderStatus.IN_PROGRESS);
      mockOrder = orderRepository.save(mockOrder);

      ProcessingContext context = new ProcessingContext(mockOrder.getId(), orderRepository);
      assertNotNull(context.buildStepChain());
      context.restorePreviousProcessingStatus();
      assertEquals(ProcessAction.PROCESS, context.getProcessAction());

      StepProcessor stepProcessor = context.getStartingStep();
      assertNotNull(stepProcessor);
      assertNull(stepProcessor.getPrev());

      int counter = 0;
      while (stepProcessor != null) {
         stepProcessor = stepProcessor.getNext();
         counter++;
      }
      assertEquals(4, counter);
   }

   @Test
   public void testParsingOrderWithPendingPostProcessStep() {
      Order mockOrder = TestUtils.createMockOrderWithPostProcessingPending();
      mockOrder.setOrderStatus(OrderStatus.IN_PROGRESS);
      mockOrder = orderRepository.save(mockOrder);

      ProcessingContext context = new ProcessingContext(mockOrder.getId(), orderRepository);
      assertNotNull(context.buildStepChain());
      context.restorePreviousProcessingStatus();
      assertEquals(ProcessAction.PROCESS, context.getProcessAction());

      StepProcessor stepProcessor = context.getStartingStep();
      assertNotNull(stepProcessor);
      assertNull(stepProcessor.getNext());

      int counter = 0;
      while (stepProcessor != null) {
         stepProcessor = stepProcessor.getPrev();
         counter++;
      }
      assertEquals(4, counter);
   }

   @Test
   public void testParsingOrderWithScheduledPostProcessStep() {
      Order mockOrder = TestUtils.createMockOrderWithPostProcessingScheduled();
      mockOrder.setOrderStatus(OrderStatus.IN_PROGRESS);
      mockOrder = orderRepository.save(mockOrder);

      ProcessingContext context = new ProcessingContext(mockOrder.getId(), orderRepository);
      assertNotNull(context.buildStepChain());
      context.restorePreviousProcessingStatus();
      assertEquals(ProcessAction.PROCESS, context.getProcessAction());

      StepProcessor stepProcessor = context.getStartingStep();
      assertNotNull(stepProcessor);
      assertNull(stepProcessor.getNext());

      int counter = 0;
      while (stepProcessor != null) {
         stepProcessor = stepProcessor.getPrev();
         counter++;
      }
      assertEquals(4, counter);
   }

   @Test
   public void testParsingRollbackOrder() {
      Order mockOrder = TestUtils.createMockOrderWithPostProcessingRollingBack();
      mockOrder.setOrderStatus(OrderStatus.ROLLING_BACK);
      mockOrder = orderRepository.save(mockOrder);

      ProcessingContext context = new ProcessingContext(mockOrder.getId(), orderRepository);
      assertNotNull(context.buildStepChain());
      context.restorePreviousProcessingStatus();
      assertEquals(ProcessAction.ROLLBACK, context.getProcessAction());

      StepProcessor stepProcessor = context.getStartingStep();
      assertNotNull(stepProcessor);
      assertNull(stepProcessor.getNext());

      int counter = 0;
      while (stepProcessor != null) {
         stepProcessor = stepProcessor.getPrev();
         counter++;
      }
      assertEquals(4, counter);
   }

   @Test
   public void testParsingOrderWithRollbackedPostProcessStep() {
      Order mockOrder = TestUtils.createMockOrderWithPostProcessingRollBacked();
      mockOrder.setOrderStatus(OrderStatus.ROLLING_BACK);
      mockOrder = orderRepository.save(mockOrder);

      ProcessingContext context = new ProcessingContext(mockOrder.getId(), orderRepository);
      assertNotNull(context.buildStepChain());
      context.restorePreviousProcessingStatus();
      assertEquals(ProcessAction.ROLLBACK, context.getProcessAction());

      StepProcessor stepProcessor = context.getStartingStep();
      assertNotNull(stepProcessor);
      assertNotNull(stepProcessor.getNext());

      int counter = 0;
      while (stepProcessor != null) {
         stepProcessor = stepProcessor.getPrev();
         counter++;
      }
      assertEquals(3, counter);
   }
}