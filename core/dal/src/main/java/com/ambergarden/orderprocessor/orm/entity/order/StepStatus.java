package com.ambergarden.orderprocessor.orm.entity.order;

/**
 * Represents the status of each order.
 * When user submits an order, all order steps will in SCHEDULED
 * state. When order processer starts to process that step, it
 * will then be marked with IN_PROGRESS state. If everything in
 * that step completes successfully, then it will be marked with
 * COMPLETE state.
 * If some error happened in one step, the order itself will be
 * marked with ROLLING_BACK, and the current step will be marked
 * with ROLLING_BACK. If roll-back success, that step will be marked
 * with ROLLBACKED.
 */
public enum StepStatus {
   SCHEDULED,
   IN_PROGRESS,
   COMPLETE,
   ROLLING_BACK,
   ROLLBACKED
}