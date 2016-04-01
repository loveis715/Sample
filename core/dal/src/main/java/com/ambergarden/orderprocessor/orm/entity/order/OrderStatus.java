package com.ambergarden.orderprocessor.orm.entity.order;

public enum OrderStatus {
   SCHEDULED,
   IN_PROGRESS,
   COMPLETE,
   ROLLING_BACK,
   FAILED
}