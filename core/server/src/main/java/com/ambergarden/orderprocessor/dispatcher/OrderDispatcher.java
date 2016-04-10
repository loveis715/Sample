package com.ambergarden.orderprocessor.dispatcher;

import static com.ambergarden.orderprocessor.Constants.PROCESS_CATEGORY_DISPATCHER;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import com.ambergarden.orderprocessor.Constants;
import com.ambergarden.orderprocessor.HomeController;
import com.ambergarden.orderprocessor.orm.entity.monitoring.ActiveProcess;
import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;
import com.ambergarden.orderprocessor.orm.repository.monitoring.ActiveProcessRepository;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;
import com.ambergarden.orderprocessor.schema.beans.monitoring.ServerMetrics;
import com.ambergarden.orderprocessor.service.MetricsService;

/**
 * Component used to dispatch orders.
 * It will periodically(5 seconds) check whether we have orders need to deal,
 * and dispatches to different nodes
 */
@Component
public class OrderDispatcher {
   private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

   private String instanceId;

   // Since we're operating order model object directly in this dispatcher,
   // we will use orderRepository
   @Autowired
   private OrderRepository orderRepository;

   @Autowired
   private ActiveProcessRepository activeProcessRepository;

   @Autowired
   private MetricsService metricsService;

   public String getInstanceId() {
      return instanceId;
   }

   public void setInstanceId(String instanceId) {
      this.instanceId = instanceId;
   }

   public void dispatch() {
      sendSystemMetrics();
      checkDispatcherStatus();

      ActiveProcess activeProcess = activeProcessRepository.findOne(PROCESS_CATEGORY_DISPATCHER);
      if (activeProcess.getProcessId().equals(instanceId)) {
         dispatchOrders();
      }
   }

   private void sendSystemMetrics() {
      // Assume that we're using http client to send the metrics data
      // to monitoring system
      ServerMetrics metrics = new ServerMetrics();
      metrics.setId(instanceId);
      metrics.setLastUpdateTime(new Date());
      metricsService.save(instanceId, metrics);
   }

   private void checkDispatcherStatus() {
      // Check active dispatcher process' state
      ActiveProcess activeProcess = activeProcessRepository.findOne(PROCESS_CATEGORY_DISPATCHER);
      if (activeProcess != null && !activeProcess.getProcessId().equals(instanceId)) {
         boolean isValid = checkServerInstanceState(activeProcess.getProcessId());
         if (!isValid) {
            makeCurrentActive();
         }
      } else if (activeProcess == null) {
         // We're starting. Try to grab the active role
         makeCurrentActive();
      }
   }

   private boolean checkServerInstanceState(String processId) {
      Date timestamp = new Date();
      timestamp = DateUtils.addMinutes(timestamp, -1);

      ServerMetrics serverMetrics = metricsService.findById(processId);
      return serverMetrics.getLastUpdateTime().after(timestamp);
   }

   private void makeCurrentActive() {
      ActiveProcess activeProcess = activeProcessRepository.findOne(PROCESS_CATEGORY_DISPATCHER);
      if (activeProcess == null) {
         activeProcess = new ActiveProcess();
         activeProcess.setCategory(PROCESS_CATEGORY_DISPATCHER);
      }
      activeProcess.setProcessId(instanceId);

      try {
         activeProcessRepository.save(activeProcess);
      } catch (ObjectOptimisticLockingFailureException ex) {
         // Another process has grabbed the active role
         // This happends only when we're starting our system
      }
   }

   private void dispatchOrders() {
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
      // We should first detect whether the target worker node is active.
      String nodeId = Constants.PROCESSING_NODE_NAME;
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

      // Write log for debugging
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