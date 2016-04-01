package com.ambergarden.orderprocessor.orm.entity.order;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ambergarden.orderprocessor.orm.entity.AbstractVersionedEntity;

/**
 * Represents the order for persistence
 * Different steps should use different types. We will work on it later
 */
@Entity
@Table(name="USER_ORDER")
public class Order extends AbstractVersionedEntity {
   @Enumerated(EnumType.STRING)
   private OrderStatus orderStatus;

   @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
   @JoinColumn(name="SCHEDULING_STEP_ID")
   private OrderStep schedulingStep;

   @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
   @JoinColumn(name="PRE_PROCESSING_STEP_ID")
   private OrderStep preProcessingStep;

   @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
   @JoinColumn(name="PROCESSING_STEP_ID")
   private OrderStep processingStep;

   @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
   @JoinColumn(name="POST_PROCESSING_STEP_ID")
   private OrderStep postProcessingStep;

   public OrderStatus getOrderStatus() {
      return orderStatus;
   }

   public void setOrderStatus(OrderStatus orderStatus) {
      this.orderStatus = orderStatus;
   }

   public OrderStep getSchedulingStep() {
      return schedulingStep;
   }

   public void setSchedulingStep(OrderStep schedulingStep) {
      this.schedulingStep = schedulingStep;
   }

   public OrderStep getPreprocessingStep() {
      return preProcessingStep;
   }

   public void setPreprocessingStep(OrderStep preProcessingStep) {
      this.preProcessingStep = preProcessingStep;
   }

   public OrderStep getProcessingStep() {
      return processingStep;
   }

   public void setProcessingStep(OrderStep processingStep) {
      this.processingStep = processingStep;
   }

   public OrderStep getPostProcessingStep() {
      return postProcessingStep;
   }

   public void setPostProcessingStep(OrderStep postProcessingStep) {
      this.postProcessingStep = postProcessingStep;
   }
}