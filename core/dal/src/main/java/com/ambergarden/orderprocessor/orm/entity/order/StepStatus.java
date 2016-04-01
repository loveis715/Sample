package com.ambergarden.orderprocessor.orm.entity.order;

public enum StepStatus {
   SCHEDULED,
   IN_PROGRESS,
   COMPLETE,
   FAIL,
   ROLLING_BACK,
   ROLLBACKED
}