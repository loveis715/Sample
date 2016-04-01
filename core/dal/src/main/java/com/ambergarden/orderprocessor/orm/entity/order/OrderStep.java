package com.ambergarden.orderprocessor.orm.entity.order;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.ambergarden.orderprocessor.orm.entity.AbstractEntity;

/**
 * Represents one step in order processing
 */
@Entity
public class OrderStep extends AbstractEntity {
   @Enumerated(EnumType.STRING)
   private StepStatus stepStatus;

   public StepStatus getStepStatus() {
      return stepStatus;
   }

   public void setStepStatus(StepStatus stepStatus) {
      this.stepStatus = stepStatus;
   }
}