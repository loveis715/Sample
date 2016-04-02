package com.ambergarden.orderprocessor.dispatcher;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import com.ambergarden.orderprocessor.HomeController;
import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;

/**
 * Component used to dispatch orders.
 * It will periodically(5 seconds) check whether we have orders need to deal,
 * and dispatches to different nodes
 */
@Component
public class OrderDispatcher {
   private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

   // Since we're operating order model object directly in this dispatcher,
   // we will use orderRepository
   @Autowired
   private OrderRepository orderRepository;

   public void dispatch() {
      // Retrieve all orders which is in IN_PROGRESS state but has no update in 1 minute.
      // It means that the order's processor has been down or has no time to deal with it.
      Date timestamp = new Date();
      timestamp = DateUtils.addMinutes(timestamp, -1);
      List<Order> blockedOrders = orderRepository.findAllByLastUpdateTimeAndOrderStatus(timestamp, OrderStatus.IN_PROGRESS);
      for (Order order : blockedOrders) {
         dispatchOrder(order);
      }

      // Retrieve all blocked rollbacks and dispatch them
      List<Order> blockedRoolbacks = orderRepository.findAllByLastUpdateTimeAndOrderStatus(timestamp, OrderStatus.ROLLING_BACK);
      for (Order order : blockedRoolbacks) {
         dispatchOrder(order);
      }

      List<Order> scheduledOrders = orderRepository.findAllByOrderStatus(OrderStatus.SCHEDULED);
      for (Order order : scheduledOrders) {
         // Set order status to in progress, which means we have started processing that order
         order.setOrderStatus(OrderStatus.IN_PROGRESS);
         dispatchOrder(order);
      }
   }

   // Dispatches the order to the order processors. We should perform load
   // balancing here. A simple (weighted) round robin is enough, either written
   // by ourself or LVS, HAProxy etc.
   // Relative huge work to do, e.g. Fault Detection etc.
   private void dispatchOrder(Order order) {
      String nodeId = "1";
      Date timestamp = new Date();
      try {
         // Update the processing node and last update time. Then
         // we will not re-dispatch these orders again.
         order.setProcessingNode(nodeId);
         order.setLastUpdateTime(timestamp);
         orderRepository.save(order);
      } catch (ObjectOptimisticLockingFailureException ex) {
         // Someone has modified the order object. It means some
         // node continued processing that order. So just return
         return;
      }

      // Write audit log for debugging
      switch(order.getOrderStatus()) {
      case IN_PROGRESS:
         logger.info("Order " + order.getId() + " resumed processing in node " + nodeId);
         break;
      case ROLLING_BACK:
         logger.info("Order " + order.getId() + " resumed rolling back in node " + nodeId);
         break;
      case SCHEDULED:
         logger.info("Order " + order.getId() + " dispatched to node " + nodeId);
         break;
      default:
         throw new UnsupportedOperationException("Invalid node status handled to dispatch");
      }
   }
}