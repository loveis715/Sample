package com.ambergarden.orderprocessor;

import java.util.Date;

import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStep;
import com.ambergarden.orderprocessor.orm.entity.order.StepStatus;

// Utils class for tests
public class TestUtils {

   // Create a mock order with pre-defined status
   public static Order createMockOrder() {
      Date timestamp = new Date();

      Order mockOrder = new Order();
      mockOrder.setOrderStatus(OrderStatus.SCHEDULED);
      mockOrder.setStartTime(timestamp);
      mockOrder.setLastUpdateTime(timestamp);
      mockOrder.setSchedulingStep(createMockStep());
      mockOrder.setPreprocessingStep(createMockStep());
      mockOrder.setProcessingStep(createMockStep());
      mockOrder.setPostProcessingStep(createMockStep());
      return mockOrder;
   }

   public static Order createMockOrderWithPostProcessingScheduled() {
      Date timestamp = new Date();

      Order mockOrder = new Order();
      mockOrder.setOrderStatus(OrderStatus.SCHEDULED);
      mockOrder.setStartTime(timestamp);
      mockOrder.setLastUpdateTime(timestamp);
      mockOrder.setSchedulingStep(createCompletedMockStep(timestamp));
      mockOrder.setPreprocessingStep(createCompletedMockStep(timestamp));
      mockOrder.setProcessingStep(createCompletedMockStep(timestamp));
      mockOrder.setPostProcessingStep(createMockStep());
      return mockOrder;
   }

   public static Order createMockOrderWithPostProcessingPending() {
      Order mockOrder = createMockOrderWithPostProcessingScheduled();
      Date timestamp = mockOrder.getLastUpdateTime();
      OrderStep orderStep = mockOrder.getPostProcessingStep();
      orderStep.setStartTime(timestamp);
      orderStep.setLastUpdateTime(timestamp);
      return mockOrder;
   }

   public static Order createMockOrderWithPostProcessingRollingBack() {
      Order mockOrder = createMockOrderWithPostProcessingScheduled();
      Date timestamp = mockOrder.getLastUpdateTime();
      OrderStep orderStep = mockOrder.getPostProcessingStep();
      orderStep.setStepStatus(StepStatus.ROLLING_BACK);
      orderStep.setStartTime(timestamp);
      orderStep.setLastUpdateTime(timestamp);
      return mockOrder;
   }

   public static Order createMockOrderWithPostProcessingRollBacked() {
      Order mockOrder = createMockOrderWithPostProcessingScheduled();
      Date timestamp = mockOrder.getLastUpdateTime();
      OrderStep orderStep = mockOrder.getPostProcessingStep();
      orderStep.setStepStatus(StepStatus.ROLLBACKED);
      orderStep.setStartTime(timestamp);
      orderStep.setLastUpdateTime(timestamp);
      return mockOrder;
   }

   private static OrderStep createMockStep() {
      OrderStep mockStep = new OrderStep();
      mockStep.setStepStatus(StepStatus.SCHEDULED);
      return mockStep;
   }

   private static OrderStep createCompletedMockStep(Date timestamp) {
      OrderStep mockStep = new OrderStep();
      mockStep.setStartTime(timestamp);
      mockStep.setLastUpdateTime(timestamp);
      mockStep.setStepStatus(StepStatus.COMPLETE);
      return mockStep;
   }
}