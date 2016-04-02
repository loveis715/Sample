package com.ambergarden.orderprocessor.dispatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStep;
import com.ambergarden.orderprocessor.orm.entity.order.StepStatus;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/spring/test-context.xml" })
public class OrderDispatcherTest {
   // The previously assigned node for mock order. Assumes this node
   // has been failed.
   private static String PREV_ASSIGNED_NODE = "100";

   @Autowired
   private OrderRepository orderRepository;

   @Autowired
   private OrderDispatcher orderDispatcher;

   @Test
   public void testDispatching() {
      // Create the mock order for dispatching
      Order mockOrder = createMockOrder();
      mockOrder = orderRepository.save(mockOrder);
      assertNull(mockOrder.getProcessingNode());

      // Dispatch that order
      orderDispatcher.dispatch();

      // Verify that the order has been dispatched
      Order processedOrder = orderRepository.findOne(mockOrder.getId());
      assertNotNull(processedOrder.getProcessingNode());

      // Verify that the order we have just dispatches will not be dispatched again
      processedOrder.setProcessingNode(PREV_ASSIGNED_NODE);
      mockOrder = orderRepository.save(processedOrder);

      orderDispatcher.dispatch();

      processedOrder = orderRepository.findOne(mockOrder.getId());
      assertEquals(mockOrder.getProcessingNode(), processedOrder.getProcessingNode());
   }

   @Test
   public void testDispatchBlockedOrders() {
      // Create the mock order for test
      Order mockOrder = createMockOrder();
      mockOrder = orderRepository.save(mockOrder);
      assertNull(mockOrder.getProcessingNode());

      // Manually modify the mock order to make it a "previously assigned order"
      Date timestamp = mockOrder.getLastUpdateTime();
      timestamp = DateUtils.addMinutes(timestamp, -2);
      mockOrder.setOrderStatus(OrderStatus.IN_PROGRESS);
      mockOrder.setProcessingNode(PREV_ASSIGNED_NODE);
      mockOrder.setLastUpdateTime(timestamp);
      mockOrder = orderRepository.save(mockOrder);

      orderDispatcher.dispatch();

      // Verifies that the order dispatcher has changed the node for processing
      Order processedOrder = orderRepository.findOne(mockOrder.getId());
      assertEquals(OrderStatus.IN_PROGRESS, processedOrder.getOrderStatus());
      assertTrue(processedOrder.getProcessingNode().compareTo(PREV_ASSIGNED_NODE) != 0);

      // Modify the processing node and verifies the newly dispatched blocking
      // order will not be dispatched again
      processedOrder.setProcessingNode(PREV_ASSIGNED_NODE);
      processedOrder = orderRepository.save(processedOrder);

      orderDispatcher.dispatch();

      assertEquals(PREV_ASSIGNED_NODE, processedOrder.getProcessingNode());
   }

   @Test
   public void testDispatchBlockedRollbacks() {
      // Create the mock order for test
      Order mockOrder = createMockOrder();
      mockOrder = orderRepository.save(mockOrder);
      assertNull(mockOrder.getProcessingNode());

      // Manually modify the mock order to make it a "previously assigned order"
      Date timestamp = mockOrder.getLastUpdateTime();
      timestamp = DateUtils.addMinutes(timestamp, -2);
      mockOrder.setOrderStatus(OrderStatus.ROLLING_BACK);
      mockOrder.setProcessingNode(PREV_ASSIGNED_NODE);
      mockOrder.setLastUpdateTime(timestamp);
      mockOrder = orderRepository.save(mockOrder);

      orderDispatcher.dispatch();

      // Verifies that the order dispatcher has changed the node for processing
      Order processedOrder = orderRepository.findOne(mockOrder.getId());
      assertEquals(OrderStatus.ROLLING_BACK, processedOrder.getOrderStatus());
      assertTrue(processedOrder.getProcessingNode().compareTo(PREV_ASSIGNED_NODE) != 0);

      // Modify the processing node and verifies the newly dispatched rolling back
      // order will not be dispatched again
      processedOrder.setProcessingNode(PREV_ASSIGNED_NODE);
      processedOrder = orderRepository.save(processedOrder);

      orderDispatcher.dispatch();

      assertEquals(PREV_ASSIGNED_NODE, processedOrder.getProcessingNode());
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