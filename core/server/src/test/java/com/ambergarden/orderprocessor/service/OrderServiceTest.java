package com.ambergarden.orderprocessor.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.orderprocessor.schema.beans.order.Order;
import com.ambergarden.orderprocessor.schema.beans.order.OrderStatus;
import com.ambergarden.orderprocessor.schema.beans.order.OrderStep;
import com.ambergarden.orderprocessor.schema.beans.order.StepStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/spring/test-context.xml" })
public class OrderServiceTest {
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

   private void verifyCreatedOrder(Order order) {
      assertTrue(order.getId() != -1);
      assertEquals(order.getCreateTime(), order.getLastUpdateTime());
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
      assertEquals(orderStep.getCreateTime(), orderStep.getLastUpdateTime());
   }

   private void verifyOrderEquality(Order expected, Order result) {
      assertEquals(expected.getId(), result.getId());
      assertEquals(expected.getOrderStatus(), result.getOrderStatus());
      assertEquals(expected.getCreateTime(), result.getCreateTime());
      assertEquals(expected.getLastUpdateTime(), result.getLastUpdateTime());

      verifyOrderStepEquality(expected.getSchedulingStep(), result.getSchedulingStep());
      verifyOrderStepEquality(expected.getPreProcessingStep(), result.getPreProcessingStep());
      verifyOrderStepEquality(expected.getProcessingStep(), result.getProcessingStep());
      verifyOrderStepEquality(expected.getPostProcessingStep(), result.getPostProcessingStep());
   }

   private void verifyOrderStepEquality(OrderStep expected, OrderStep result) {
      assertEquals(expected.getId(), result.getId());
      assertEquals(expected.getStepStatus(), result.getStepStatus());
      assertEquals(expected.getCreateTime(), result.getCreateTime());
      assertEquals(expected.getLastUpdateTime(), result.getLastUpdateTime());
   }
}