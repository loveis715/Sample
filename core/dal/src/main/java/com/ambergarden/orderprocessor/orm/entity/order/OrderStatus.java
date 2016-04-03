package com.ambergarden.orderprocessor.orm.entity.order;

/**
 * Represents the order's status.
 *
 * When user submits an order, it will first be marked as SCHEDULED.
 * All order steps will also be marked as SCHEDULED, with start time
 * and last update time left empty.
 *
 * When dispatcher has decided to process that order on a specific
 * node, it will be marked with IN_PROGRESS. The order processing
 * engine on that specific node will take that order and process it.
 * If all steps works well, then the order will be marked with COMPLETE.
 * Each order step will first be marked with IN_PROGRESS, then COMPLETE.
 *
 * If any order step has gone wrong, we should mark the order to be
 * ROLLING_BACK immediately, and start to roll back current order step.
 * The current order step will have a state called ROLLING_BACK. After
 * current order step has been rolled back, then it will be marked as
 * ROLLBACKED. And the order processing engine will start to roll back
 * the previous step.
 */
public enum OrderStatus {
   SCHEDULED,
   IN_PROGRESS,
   COMPLETE,
   ROLLING_BACK,
   FAILED
}