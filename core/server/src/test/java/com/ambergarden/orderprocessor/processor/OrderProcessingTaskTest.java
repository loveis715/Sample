package com.ambergarden.orderprocessor.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.orderprocessor.TestUtils;
import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStep;
import com.ambergarden.orderprocessor.orm.entity.order.StepStatus;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;
import com.ambergarden.orderprocessor.processor.steps.MockStepConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/spring/order-test-context.xml" })
public class OrderProcessingTaskTest {
   @Autowired
   private OrderRepository orderRepository;

   @Test
   public void testProcessingOrderSuccessfully() {
      // All steps will pass
      MockStepConfig.schedulingStepSuccess = true;
      MockStepConfig.preProcessingStepSuccess = true;
      MockStepConfig.processingStepSuccess = true;
      MockStepConfig.postProcessingStepSuccess = true;

      // Create a mock order
      Order mockOrder = TestUtils.createMockOrder();
      mockOrder.setOrderStatus(OrderStatus.IN_PROGRESS);
      mockOrder = orderRepository.save(mockOrder);

      // Process that mock order
      OrderProcessingTask processingTask = createOrderProcessingTask(mockOrder.getId(), orderRepository);
      processingTask.run();

      // Verify that the order has been processed successfully
      mockOrder = orderRepository.findOne(mockOrder.getId());
      assertEquals(OrderStatus.COMPLETE, mockOrder.getOrderStatus());
      assertEquals(null, mockOrder.getProcessingNode());

      // Verify steps
      OrderStep schedulingStep = mockOrder.getSchedulingStep();
      assertEquals(StepStatus.COMPLETE, schedulingStep.getStepStatus());
      OrderStep preProcessingStep = mockOrder.getPreprocessingStep();
      assertEquals(StepStatus.COMPLETE, preProcessingStep.getStepStatus());
      OrderStep processingStep = mockOrder.getProcessingStep();
      assertEquals(StepStatus.COMPLETE, processingStep.getStepStatus());
      OrderStep postProcessingStep = mockOrder.getPostProcessingStep();
      assertEquals(StepStatus.COMPLETE, postProcessingStep.getStepStatus());
      assertEquals(mockOrder.getLastUpdateTime(), postProcessingStep.getLastUpdateTime());
   }

   @Test
   public void testResumeWithPostProcessingScheduledSuccessfully() {
      // All steps will pass
      MockStepConfig.schedulingStepSuccess = true;
      MockStepConfig.preProcessingStepSuccess = true;
      MockStepConfig.processingStepSuccess = true;
      MockStepConfig.postProcessingStepSuccess = true;

      // Create a mock order with only post-processing left scheduled
      Order mockOrder = TestUtils.createMockOrderWithPostProcessingScheduled();
      mockOrder.setOrderStatus(OrderStatus.IN_PROGRESS);
      mockOrder = orderRepository.save(mockOrder);

      // Process that mock order
      OrderProcessingTask processingTask = createOrderProcessingTask(mockOrder.getId(), orderRepository);
      processingTask.run();

      // Verify that the order has been processed successfully
      mockOrder = orderRepository.findOne(mockOrder.getId());
      assertEquals(OrderStatus.COMPLETE, mockOrder.getOrderStatus());
      assertEquals(null, mockOrder.getProcessingNode());

      // Verify steps
      OrderStep schedulingStep = mockOrder.getSchedulingStep();
      assertEquals(StepStatus.COMPLETE, schedulingStep.getStepStatus());
      OrderStep preProcessingStep = mockOrder.getPreprocessingStep();
      assertEquals(StepStatus.COMPLETE, preProcessingStep.getStepStatus());
      OrderStep processingStep = mockOrder.getProcessingStep();
      assertEquals(StepStatus.COMPLETE, processingStep.getStepStatus());
      OrderStep postProcessingStep = mockOrder.getPostProcessingStep();
      assertEquals(StepStatus.COMPLETE, postProcessingStep.getStepStatus());
      assertEquals(mockOrder.getLastUpdateTime(), postProcessingStep.getLastUpdateTime());
   }

   @Test
   public void testResumeWithPostProcessingPendingSuccessfully() {
      // All steps will pass
      MockStepConfig.schedulingStepSuccess = true;
      MockStepConfig.preProcessingStepSuccess = true;
      MockStepConfig.processingStepSuccess = true;
      MockStepConfig.postProcessingStepSuccess = true;

      // Create a mock order with only post-processing left in progress
      Order mockOrder = TestUtils.createMockOrderWithPostProcessingPending();
      mockOrder.setOrderStatus(OrderStatus.IN_PROGRESS);
      mockOrder = orderRepository.save(mockOrder);

      // Process that mock order
      OrderProcessingTask processingTask = createOrderProcessingTask(mockOrder.getId(), orderRepository);
      processingTask.run();

      // Verify that the order has been processed successfully
      mockOrder = orderRepository.findOne(mockOrder.getId());
      assertEquals(OrderStatus.COMPLETE, mockOrder.getOrderStatus());
      assertEquals(null, mockOrder.getProcessingNode());

      // Verify steps
      OrderStep schedulingStep = mockOrder.getSchedulingStep();
      assertEquals(StepStatus.COMPLETE, schedulingStep.getStepStatus());
      OrderStep preProcessingStep = mockOrder.getPreprocessingStep();
      assertEquals(StepStatus.COMPLETE, preProcessingStep.getStepStatus());
      OrderStep processingStep = mockOrder.getProcessingStep();
      assertEquals(StepStatus.COMPLETE, processingStep.getStepStatus());
      OrderStep postProcessingStep = mockOrder.getPostProcessingStep();
      assertEquals(StepStatus.COMPLETE, postProcessingStep.getStepStatus());
      assertEquals(mockOrder.getLastUpdateTime(), postProcessingStep.getLastUpdateTime());
   }

   @Test
   public void testResumeWithRollback() {
      // All steps will pass
      MockStepConfig.schedulingStepSuccess = true;
      MockStepConfig.preProcessingStepSuccess = true;
      MockStepConfig.processingStepSuccess = true;
      MockStepConfig.postProcessingStepSuccess = true;

      // Create a mock order with only post-processing left rolling back
      Order mockOrder = TestUtils.createMockOrderWithPostProcessingRollingBack();
      mockOrder.setOrderStatus(OrderStatus.ROLLING_BACK);
      mockOrder = orderRepository.save(mockOrder);

      // Process that mock order
      OrderProcessingTask processingTask = createOrderProcessingTask(mockOrder.getId(), orderRepository);
      processingTask.run();

      // Verify that the order has been marked as failed
      mockOrder = orderRepository.findOne(mockOrder.getId());
      assertEquals(OrderStatus.FAILED, mockOrder.getOrderStatus());
      assertEquals(null, mockOrder.getProcessingNode());

      // Verify steps
      OrderStep schedulingStep = mockOrder.getSchedulingStep();
      assertEquals(StepStatus.ROLLBACKED, schedulingStep.getStepStatus());
      assertEquals(mockOrder.getLastUpdateTime(), schedulingStep.getLastUpdateTime());
      OrderStep preProcessingStep = mockOrder.getPreprocessingStep();
      assertEquals(StepStatus.ROLLBACKED, preProcessingStep.getStepStatus());
      OrderStep processingStep = mockOrder.getProcessingStep();
      assertEquals(StepStatus.ROLLBACKED, processingStep.getStepStatus());
      OrderStep postProcessingStep = mockOrder.getPostProcessingStep();
      assertEquals(StepStatus.ROLLBACKED, postProcessingStep.getStepStatus());
   }

   @Test
   public void testResumeWithPostProcessingRollbacked() {
      // All steps will pass
      MockStepConfig.schedulingStepSuccess = true;
      MockStepConfig.preProcessingStepSuccess = true;
      MockStepConfig.processingStepSuccess = true;
      MockStepConfig.postProcessingStepSuccess = true;

      // Create a mock order with only post-processing left rolling back
      Order mockOrder = TestUtils.createMockOrderWithPostProcessingRollBacked();
      mockOrder.setOrderStatus(OrderStatus.ROLLING_BACK);
      mockOrder = orderRepository.save(mockOrder);

      // Process that mock order
      OrderProcessingTask processingTask = createOrderProcessingTask(mockOrder.getId(), orderRepository);
      processingTask.run();

      // Verify that the order has been marked as failed
      mockOrder = orderRepository.findOne(mockOrder.getId());
      assertEquals(OrderStatus.FAILED, mockOrder.getOrderStatus());
      assertEquals(null, mockOrder.getProcessingNode());

      // Verify steps
      OrderStep schedulingStep = mockOrder.getSchedulingStep();
      assertEquals(StepStatus.ROLLBACKED, schedulingStep.getStepStatus());
      assertEquals(mockOrder.getLastUpdateTime(), schedulingStep.getLastUpdateTime());
      OrderStep preProcessingStep = mockOrder.getPreprocessingStep();
      assertEquals(StepStatus.ROLLBACKED, preProcessingStep.getStepStatus());
      OrderStep processingStep = mockOrder.getProcessingStep();
      assertEquals(StepStatus.ROLLBACKED, processingStep.getStepStatus());
      OrderStep postProcessingStep = mockOrder.getPostProcessingStep();
      assertEquals(StepStatus.ROLLBACKED, postProcessingStep.getStepStatus());
   }

   @Test
   public void testProcessingOrderUnsuccessful() {
      // All steps will pass
      MockStepConfig.schedulingStepSuccess = true;
      MockStepConfig.preProcessingStepSuccess = true;
      MockStepConfig.processingStepSuccess = true;
      MockStepConfig.postProcessingStepSuccess = false;

      // Create a mock order
      Order mockOrder = TestUtils.createMockOrder();
      mockOrder.setOrderStatus(OrderStatus.IN_PROGRESS);
      mockOrder = orderRepository.save(mockOrder);

      // Process that mock order
      OrderProcessingTask processingTask = createOrderProcessingTask(mockOrder.getId(), orderRepository);
      processingTask.run();

      // Verify that the order has been marked as failed
      mockOrder = orderRepository.findOne(mockOrder.getId());
      assertEquals(OrderStatus.FAILED, mockOrder.getOrderStatus());
      assertEquals(null, mockOrder.getProcessingNode());

      // Verify steps
      OrderStep schedulingStep = mockOrder.getSchedulingStep();
      assertEquals(StepStatus.ROLLBACKED, schedulingStep.getStepStatus());
      assertEquals(mockOrder.getLastUpdateTime(), schedulingStep.getLastUpdateTime());
      OrderStep preProcessingStep = mockOrder.getPreprocessingStep();
      assertEquals(StepStatus.ROLLBACKED, preProcessingStep.getStepStatus());
      OrderStep processingStep = mockOrder.getProcessingStep();
      assertEquals(StepStatus.ROLLBACKED, processingStep.getStepStatus());
      OrderStep postProcessingStep = mockOrder.getPostProcessingStep();
      assertEquals(StepStatus.ROLLBACKED, postProcessingStep.getStepStatus());
   }

   @Test
   public void testResumeProcessingOrderUnsuccessful() {
      // All steps will pass
      MockStepConfig.schedulingStepSuccess = true;
      MockStepConfig.preProcessingStepSuccess = true;
      MockStepConfig.processingStepSuccess = true;
      MockStepConfig.postProcessingStepSuccess = false;

      // Create a mock order
      Order mockOrder = TestUtils.createMockOrderWithPostProcessingPending();
      mockOrder.setOrderStatus(OrderStatus.IN_PROGRESS);
      mockOrder = orderRepository.save(mockOrder);

      // Process that mock order
      OrderProcessingTask processingTask = createOrderProcessingTask(mockOrder.getId(), orderRepository);
      processingTask.run();

      // Verify that the order has been marked as failed
      mockOrder = orderRepository.findOne(mockOrder.getId());
      assertEquals(OrderStatus.FAILED, mockOrder.getOrderStatus());
      assertEquals(null, mockOrder.getProcessingNode());

      // Verify steps
      OrderStep schedulingStep = mockOrder.getSchedulingStep();
      assertEquals(StepStatus.ROLLBACKED, schedulingStep.getStepStatus());
      assertEquals(mockOrder.getLastUpdateTime(), schedulingStep.getLastUpdateTime());
      OrderStep preProcessingStep = mockOrder.getPreprocessingStep();
      assertEquals(StepStatus.ROLLBACKED, preProcessingStep.getStepStatus());
      OrderStep processingStep = mockOrder.getProcessingStep();
      assertEquals(StepStatus.ROLLBACKED, processingStep.getStepStatus());
      OrderStep postProcessingStep = mockOrder.getPostProcessingStep();
      assertEquals(StepStatus.ROLLBACKED, postProcessingStep.getStepStatus());
   }

   // Create the order processing task with the mocked processing context
   private OrderProcessingTask createOrderProcessingTask(int orderId, OrderRepository orderRepository) {
      return new OrderProcessingTask(orderId, orderRepository) {
         @Override
         protected ProcessingContext createProcessingContext(int orderId, OrderRepository orderRepository) {
            return new MockProcessingContext(orderId, orderRepository);
         }
      };
   }
}