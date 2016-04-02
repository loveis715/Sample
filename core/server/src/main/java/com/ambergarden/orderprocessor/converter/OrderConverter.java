package com.ambergarden.orderprocessor.converter;

import org.springframework.stereotype.Component;

import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStep;
import com.ambergarden.orderprocessor.orm.entity.order.StepStatus;

/**
 * Converts between order's model object and data transfer object
 */
@Component
public class OrderConverter
   extends AbstractEntityListConverter<Order, com.ambergarden.orderprocessor.schema.beans.order.Order> {

   @Override
   public com.ambergarden.orderprocessor.schema.beans.order.Order convertFrom(Order mo) {
      com.ambergarden.orderprocessor.schema.beans.order.Order order
         = new com.ambergarden.orderprocessor.schema.beans.order.Order();
      order.setId(mo.getId());
      order.setOrderStatus(convertFrom(mo.getOrderStatus()));
      order.setCreateTime(mo.getCreateTime());
      order.setLastUpdateTime(mo.getLastUpdateTime());
      order.setSchedulingStep(convertFrom(mo.getSchedulingStep()));
      order.setPreProcessingStep(convertFrom(mo.getPreprocessingStep()));
      order.setProcessingStep(convertFrom(mo.getProcessingStep()));
      order.setPostProcessingStep(convertFrom(mo.getPostProcessingStep()));
      return order;
   }

   @Override
   public Order convertTo(com.ambergarden.orderprocessor.schema.beans.order.Order dto) {
      Order order = new Order();
      order.setId(dto.getId());
      order.setOrderStatus(convertTo(dto.getOrderStatus()));
      order.setCreateTime(dto.getCreateTime());
      order.setLastUpdateTime(dto.getLastUpdateTime());
      order.setSchedulingStep(convertTo(dto.getSchedulingStep()));
      order.setPreprocessingStep(convertTo(dto.getPreProcessingStep()));
      order.setProcessingStep(convertTo(dto.getProcessingStep()));
      order.setPostProcessingStep(convertTo(dto.getPostProcessingStep()));
      return order;
   }

   // Generic logic to convert order step model object to data transfer object/business object
   private com.ambergarden.orderprocessor.schema.beans.order.OrderStep convertFrom(OrderStep mo) {
      com.ambergarden.orderprocessor.schema.beans.order.OrderStep orderStep
         = new com.ambergarden.orderprocessor.schema.beans.order.OrderStep();
      orderStep.setId(mo.getId());
      orderStep.setStepStatus(convertFrom(mo.getStepStatus()));
      orderStep.setCreateTime(mo.getCreateTime());
      orderStep.setLastUpdateTime(mo.getLastUpdateTime());
      return orderStep;
   }

   // Generic logic to convert order step data transfer object/business object to model object
   private OrderStep convertTo(com.ambergarden.orderprocessor.schema.beans.order.OrderStep dto) {
      OrderStep orderStep = new OrderStep();
      orderStep.setId(dto.getId());
      orderStep.setStepStatus(convertTo(dto.getStepStatus()));
      orderStep.setCreateTime(dto.getCreateTime());
      orderStep.setLastUpdateTime(dto.getLastUpdateTime());
      return orderStep;
   }

   // Functions to convert between enums. Tools like Dozer etc. will help
   // TODO: Try to utilize Dozer
   private com.ambergarden.orderprocessor.schema.beans.order.StepStatus convertFrom(StepStatus stepStatus) {
      switch(stepStatus) {
      case SCHEDULED: return com.ambergarden.orderprocessor.schema.beans.order.StepStatus.SCHEDULED;
      case IN_PROGRESS: return com.ambergarden.orderprocessor.schema.beans.order.StepStatus.IN_PROGRESS;
      case COMPLETE: return com.ambergarden.orderprocessor.schema.beans.order.StepStatus.COMPLETE;
      case ROLLING_BACK: return com.ambergarden.orderprocessor.schema.beans.order.StepStatus.ROLLING_BACK;
      case ROLLBACKED: return com.ambergarden.orderprocessor.schema.beans.order.StepStatus.ROLLBACKED;
      case FAIL: return com.ambergarden.orderprocessor.schema.beans.order.StepStatus.FAIL;
      default: throw new UnsupportedOperationException();
      }
   }

   private StepStatus convertTo(com.ambergarden.orderprocessor.schema.beans.order.StepStatus stepStatus) {
      switch(stepStatus) {
      case SCHEDULED: return StepStatus.SCHEDULED;
      case IN_PROGRESS: return StepStatus.IN_PROGRESS;
      case COMPLETE: return StepStatus.COMPLETE;
      case ROLLING_BACK: return StepStatus.ROLLING_BACK;
      case ROLLBACKED: return StepStatus.ROLLBACKED;
      case FAIL: return StepStatus.FAIL;
      default: throw new UnsupportedOperationException();
      }
   }

   private com.ambergarden.orderprocessor.schema.beans.order.OrderStatus convertFrom(OrderStatus orderStatus) {
      switch(orderStatus) {
      case SCHEDULED: return com.ambergarden.orderprocessor.schema.beans.order.OrderStatus.SCHEDULED;
      case IN_PROGRESS: return com.ambergarden.orderprocessor.schema.beans.order.OrderStatus.IN_PROGRESS;
      case COMPLETE: return com.ambergarden.orderprocessor.schema.beans.order.OrderStatus.COMPLETE;
      case ROLLING_BACK: return com.ambergarden.orderprocessor.schema.beans.order.OrderStatus.ROLLING_BACK;
      case FAILED: return com.ambergarden.orderprocessor.schema.beans.order.OrderStatus.FAILED;
      default: throw new UnsupportedOperationException();
      }
   }

   private OrderStatus convertTo(com.ambergarden.orderprocessor.schema.beans.order.OrderStatus orderStatus) {
      switch(orderStatus) {
      case SCHEDULED: return OrderStatus.SCHEDULED;
      case IN_PROGRESS: return OrderStatus.IN_PROGRESS;
      case COMPLETE: return OrderStatus.COMPLETE;
      case ROLLING_BACK: return OrderStatus.ROLLING_BACK;
      case FAILED: return OrderStatus.FAILED;
      default: throw new UnsupportedOperationException();
      }
   }
}