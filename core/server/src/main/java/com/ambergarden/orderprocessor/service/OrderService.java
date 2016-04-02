package com.ambergarden.orderprocessor.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ambergarden.orderprocessor.converter.OrderConverter;
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
   @Autowired
   private OrderRepository orderRepository;

   @Autowired
   private OrderConverter orderConverter;

   public List<Order> findAll() {
      return orderConverter.convertListFrom(orderRepository.findAll());
   }

   public Order create(Order order) {
      // TODO: Check whether user has specified id.
      // We need support of global event handler, to convert this case
      // into user friendly response
      updateOrder(order);

      com.ambergarden.orderprocessor.orm.entity.order.Order orderMO
         = orderConverter.convertTo(order);
      orderMO.setId(-1);
      orderMO = orderRepository.save(orderMO);
      return orderConverter.convertFrom(orderMO);
   }

   public Order findById(int id) {
      com.ambergarden.orderprocessor.orm.entity.order.Order result = orderRepository.findOne(id);
      if (result != null) {
         return orderConverter.convertFrom(result);
      } else {
         // FIXME: A proper way is to raise an exception
         return null;
      }
   }

   private void updateOrder(Order order) {
      // Update order's status, timestamp
      Date timestamp = new Date();
      order.setOrderStatus(OrderStatus.SCHEDULED);
      order.setCreateTime(timestamp);
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
      orderStep.setCreateTime(timestamp);
      orderStep.setLastUpdateTime(timestamp);
      return orderStep;
   }
}