package com.ambergarden.orderprocessor.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.orderprocessor.exception.BadOrderRequestException;
import com.ambergarden.orderprocessor.exception.OrderNotFoundException;
import com.ambergarden.orderprocessor.schema.beans.order.Order;
import com.ambergarden.orderprocessor.schema.beans.order.OrderStatus;
import com.ambergarden.orderprocessor.schema.beans.order.OrderStep;
import com.ambergarden.orderprocessor.schema.beans.order.StepStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/spring/test-context.xml" })
public class OrderServiceTest {
   private static final int INVALID_ORDER_ID = 9999;

   @Autowired
   private OrderService orderService;

   @Test
   public void testCRUD() {
      // Get existing order count
      List<Order> orderList = orderService.findAll();
      int orderCount = orderList.size();

      // Test create order
      Order order = new Order();
      order.setId(-1);
      order = orderService.create(order);
      verifyCreatedOrder(order);

      // Test find by id
      Order result = orderService.findById(order.getId());
      verifyOrderEquality(order, result);

      // Verify that the order count has been increased
      orderList = orderService.findAll();
      assertEquals(orderCount + 1, orderList.size());
   }

   @Test
   public void testCreateWithId0() {
      // Test order creation with id 0. 0 should be a valid id
      // during creation. Some of the JS libraries does not take
      // -1 as a convension for creation, so a better case should
      // take both -1 and 0 as a valid id for creation, to ease
      // developers from different JS platform
      Order order = new Order();
      order.setId(0);
      order = orderService.create(order);
      assertTrue(order.getId() != 0 && order.getId() != -1);
   }

   @Test(expected = BadOrderRequestException.class)
   public void testCreateWithInvalidId() {
      // Create order with a predefined id is not allowed
      Order order = new Order();
      order.setId(2);
      orderService.create(order);
   }

   @Test(expected = BadOrderRequestException.class)
   public void testCreateWithSteps() {
      // Create order with a predefined step is not allowed
      Order order = new Order();
      order.setId(0);
      order.setSchedulingStep(new OrderStep());
      orderService.create(order);
   }

   @Test(expected = BadOrderRequestException.class)
   public void testCreateWithStatus() {
      // Create an order with predefined status is not allowed
      Order order = new Order();
      order.setId(0);
      order.setOrderStatus(OrderStatus.SCHEDULED);
      orderService.create(order);
   }

   @Test(expected = BadOrderRequestException.class)
   public void testCreateWithTime() {
      // Create an order with predefined time is not allowed
      Order order = new Order();
      order.setId(0);
      order.setStartTime(new Date());
      orderService.create(order);
   }

   @Test(expected = OrderNotFoundException.class)
   public void testFindWithInvalidId() {
      // Find a non-existing order should cause the OrderNotFoundException
      orderService.findById(INVALID_ORDER_ID);
   }

   private void verifyCreatedOrder(Order order) {
      assertTrue(order.getId() != -1);
      assertEquals(order.getStartTime(), order.getLastUpdateTime());
      assertEquals(OrderStatus.SCHEDULED, order.getOrderStatus());

      verifyOrderStepInCreatedOrder(order.getSchedulingStep());
      verifyOrderStepInCreatedOrder(order.getPreProcessingStep());
      verifyOrderStepInCreatedOrder(order.getProcessingStep());
      verifyOrderStepInCreatedOrder(order.getPostProcessingStep());
   }

   private void verifyOrderStepInCreatedOrder(OrderStep orderStep) {
      assertNotNull(orderStep);
      assertTrue(orderStep.getId() != 0);
      assertEquals(StepStatus.SCHEDULED, orderStep.getStepStatus());
      assertEquals(orderStep.getStartTime(), orderStep.getLastUpdateTime());
   }

   private void verifyOrderEquality(Order expected, Order result) {
      assertEquals(expected.getId(), result.getId());
      assertEquals(expected.getOrderStatus(), result.getOrderStatus());
      assertEquals(expected.getStartTime(), result.getStartTime());
      assertEquals(expected.getLastUpdateTime(), result.getLastUpdateTime());

      verifyOrderStepEquality(expected.getSchedulingStep(), result.getSchedulingStep());
      verifyOrderStepEquality(expected.getPreProcessingStep(), result.getPreProcessingStep());
      verifyOrderStepEquality(expected.getProcessingStep(), result.getProcessingStep());
      verifyOrderStepEquality(expected.getPostProcessingStep(), result.getPostProcessingStep());
   }

   private void verifyOrderStepEquality(OrderStep expected, OrderStep result) {
      assertEquals(expected.getId(), result.getId());
      assertEquals(expected.getStepStatus(), result.getStepStatus());
      assertEquals(expected.getStartTime(), result.getStartTime());
      assertEquals(expected.getLastUpdateTime(), result.getLastUpdateTime());
   }
}