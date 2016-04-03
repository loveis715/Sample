package com.ambergarden.orderprocessor.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ambergarden.orderprocessor.HomeController;
import com.ambergarden.orderprocessor.converter.OrderConverter;
import com.ambergarden.orderprocessor.exception.BadOrderRequestException;
import com.ambergarden.orderprocessor.exception.OrderNotFoundException;
import com.ambergarden.orderprocessor.orm.repository.order.OrderRepository;
import com.ambergarden.orderprocessor.schema.beans.order.Order;
import com.ambergarden.orderprocessor.schema.beans.order.OrderStatus;
import com.ambergarden.orderprocessor.schema.beans.order.OrderStep;
import com.ambergarden.orderprocessor.schema.beans.order.StepStatus;

/**
 * Service for create and retrieve order information
 */
@Service
public class OrderService {
   private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

   @Autowired
   private OrderRepository orderRepository;

   @Autowired
   private OrderConverter orderConverter;

   public List<Order> findAll() {
      logger.info("OrderService - findAll() invoked");

      return orderConverter.convertListFrom(orderRepository.findAll());
   }

   public Order create(Order order) {
      logger.info("OrderService - create() invoked");

      validOrderForCreation(order);

      // TODO: Check whether user has specified id.
      // We need support of global event handler, to convert this case
      // into user friendly response
      updateOrder(order);

      com.ambergarden.orderprocessor.orm.entity.order.Order orderMO
         = orderConverter.convertTo(order);
      orderMO.setId(-1); // PostgreSQL uses -1 for creation.
      orderMO = orderRepository.save(orderMO);

      logger.info("OrderService - create() invoked successful: order id: " + order.getId());

      return orderConverter.convertFrom(orderMO);
   }

   public Order findById(int id) {
      logger.info("OrderService - findById() invoked");

      com.ambergarden.orderprocessor.orm.entity.order.Order result = orderRepository.findOne(id);
      if (result != null) {
         return orderConverter.convertFrom(result);
      } else {
         throw new OrderNotFoundException();
      }
   }

   // TODO: Move to validation framework constructed by ControllerAdvice,
   // if we have time
   private void validOrderForCreation(Order order) {
      // The default value for id field is 0, and some JS framework uses
      // -1 for creation.
      if (order.getId() != -1 && order.getId() != 0) {
         throw new BadOrderRequestException();
      }

      if (order.getOrderStatus() != null) {
         throw new BadOrderRequestException();
      }

      if (order.getStartTime() != null || order.getLastUpdateTime() != null) {
         throw new BadOrderRequestException();
      }

      // User should not specify any steps
      if (order.getSchedulingStep() != null
         || order.getPreProcessingStep() != null
         || order.getProcessingStep() != null
         || order.getPostProcessingStep() != null) {
         throw new BadOrderRequestException();
      }
   }

   private void updateOrder(Order order) {
      // Update order's status, timestamp
      Date timestamp = new Date();
      order.setOrderStatus(OrderStatus.SCHEDULED);
      order.setStartTime(timestamp);
      order.setLastUpdateTime(timestamp);

      // Generate order steps for tracking order's state
      // Each step should contain different data in real-world
      // case
      order.setSchedulingStep(createFakeOrderStep(timestamp));
      order.setPreProcessingStep(createFakeOrderStep(timestamp));
      order.setProcessingStep(createFakeOrderStep(timestamp));
      order.setPostProcessingStep(createFakeOrderStep(timestamp));
   }

   private OrderStep createFakeOrderStep(Date timestamp) {
      OrderStep orderStep = new OrderStep();
      orderStep.setStepStatus(StepStatus.SCHEDULED);
      return orderStep;
   }
}