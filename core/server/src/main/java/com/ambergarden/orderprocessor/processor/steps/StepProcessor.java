package com.ambergarden.orderprocessor.processor.steps;

import com.ambergarden.orderprocessor.orm.entity.order.StepStatus;

/**
 * Interface for concrete processing steps
 */
public interface StepProcessor {
   /**
    * Performs processing logic for current step
    * @return the process result
    */
   StepStatus process();

   /**
    * Performs rollback logic for current step
    * @return the rollback result
    */
   StepStatus rollback();

   /**
    * Get the next step for processing orders
    * @return the next step for processing orders
    */
   StepProcessor getNext();

   /**
    * Get the previous step for rolling back
    * @return the previous step for rolling back
    */
   StepProcessor getPrev();
}