package com.ambergarden.orderprocessor.orm.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStep;
import com.ambergarden.orderprocessor.orm.entity.order.StepStatus;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/META-INF/spring/db/dal-*-context.xml" })
public class OrderRepositoryTest {
   @Autowired
   private OrderRepository orderRepository;

   @Test
   public void testOrderCRUD() {
      // Test create
      Order order = createMockOrder();
      Order savedOrder = orderRepository.save(order);
      verifyOrderEquality(order, savedOrder);

      // Test retrieve
      order = orderRepository.findOne(savedOrder.getId());

      // Test update
      Date timestamp = new Date();
      order.setLastUpdateTime(timestamp);
      OrderStep schedulingStep = order.getSchedulingStep();
      schedulingStep.setLastUpdateTime(timestamp);
      schedulingStep.setStepStatus(StepStatus.IN_PROGRESS);
      savedOrder = orderRepository.save(order);
      verifyOrderEquality(order, savedOrder);

      // Test delete
      orderRepository.delete(savedOrder.getId());
      order = orderRepository.findOne(savedOrder.getId());
      assertNull(order);
   }

   @Test(expected = ObjectOptimisticLockingFailureException.class)
   public void testOptimisticLock() {
      // Create a new order and save it, version should be 1
      Order order = createMockOrder();
      Order savedOrder = orderRepository.save(order);
      assertEquals(0, savedOrder.getLockVersion());

      // Update & save that order again, the version should be increased by 1
      savedOrder.setOrderStatus(OrderStatus.IN_PROGRESS);
      savedOrder = orderRepository.save(savedOrder);
      assertEquals(1, savedOrder.getLockVersion());

      // Using a previous version should throw an ObjectOptimisticLockingFailureException
      savedOrder.setLockVersion(0);
      orderRepository.save(savedOrder);
   }

   @Test(expected = DataIntegrityViolationException.class)
   public void testCreateWithoutOrderStatus() {
      // Create a new order without status and save it, a
      // DataIntegrityViolationException should be thrown
      Order order = createMockOrder();
      order.setOrderStatus(null);
      orderRepository.save(order);
   }

   @Test(expected = DataIntegrityViolationException.class)
   public void testCreateWithoutStatusInOrderStep() {
      // Create a new order without status in order step and save it, a
      // DataIntegrityViolationException should be thrown
      Order order = createMockOrder();
      order.getSchedulingStep().setStepStatus(null);
      orderRepository.save(order);
   }

   @Test(expected = DataIntegrityViolationException.class)
   public void testCreateWithoutSchedulingStep() {
      // Create a new order without scheduling step and save it,
      // a DataIntegrityViolationException should be thrown, to
      // ensure the not null constraint in our database does not
      // broken
      Order order = createMockOrder();
      order.setSchedulingStep(null);
      orderRepository.save(order);
   }

   @Test(expected = DataIntegrityViolationException.class)
   public void testCreateWithoutPreprocessingStep() {
      // Create a new order without pre-processing step and save it,
      // a DataIntegrityViolationException should be thrown
      Order order = createMockOrder();
      order.setPreprocessingStep(null);
      orderRepository.save(order);
   }

   @Test(expected = DataIntegrityViolationException.class)
   public void testCreateWithoutProcessingStep() {
      // Create a new order without processing step and save it,
      // a DataIntegrityViolationException should be thrown
      Order order = createMockOrder();
      order.setProcessingStep(null);
      orderRepository.save(order);
   }

   @Test(expected = DataIntegrityViolationException.class)
   public void testCreateWithoutPostprocessingStep() {
      // Create a new order without post-processing step and save it,
      // a DataIntegrityViolationException should be thrown
      Order order = createMockOrder();
      order.setPostProcessingStep(null);
      orderRepository.save(order);
   }

   private void verifyOrderEquality(Order expected, Order result) {
      assertEquals(expected.getCreateTime(), result.getCreateTime());
      assertEquals(expected.getLastUpdateTime(), result.getLastUpdateTime());
      assertEquals(expected.getOrderStatus(), result.getOrderStatus());

      assertNotNull(result.getSchedulingStep());
      verifyStepEquality(expected.getSchedulingStep(), result.getSchedulingStep());

      assertNotNull(result.getPreprocessingStep());
      verifyStepEquality(expected.getPreprocessingStep(), result.getPreprocessingStep());

      assertNotNull(result.getProcessingStep());
      verifyStepEquality(expected.getProcessingStep(), result.getProcessingStep());

      assertNotNull(result.getPostProcessingStep());
      verifyStepEquality(expected.getPostProcessingStep(), result.getPostProcessingStep());
   }

   private void verifyStepEquality(OrderStep expected, OrderStep result) {
      assertEquals(expected.getCreateTime(), result.getCreateTime());
      assertEquals(expected.getLastUpdateTime(), result.getLastUpdateTime());
      assertEquals(expected.getStepStatus(), result.getStepStatus());
   }

   private Order createMockOrder() {
      Date timestamp = new Date();

      Order mockOrder = new Order();
      mockOrder.setOrderStatus(OrderStatus.SCHEDULED);
      mockOrder.setCreateTime(timestamp);
      mockOrder.setLastUpdateTime(timestamp);
      mockOrder.setSchedulingStep(createMockStep(timestamp));
      mockOrder.setPreprocessingStep(createMockStep(timestamp));
      mockOrder.setProcessingStep(createMockStep(timestamp));
      mockOrder.setPostProcessingStep(createMockStep(timestamp));
      return mockOrder;
   }

   private OrderStep createMockStep(Date timestamp) {
      OrderStep mockStep = new OrderStep();
      mockStep.setCreateTime(timestamp);
      mockStep.setLastUpdateTime(timestamp);
      mockStep.setStepStatus(StepStatus.SCHEDULED);
      return mockStep;
   }
}