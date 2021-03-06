package com.ambergarden.orderprocessor;

/**
 * Class used to hold all constants
 */
public class Constants {
   // API URLs
   public final static String ORDERS_URL = "/api/orders";
   public final static String METRICS_URL = "/api/metrics";

   // URL constants
   public final static String FIND_BY_ID_URL = "/{id}";
   public final static String SAVE_BY_ID_URL = "/{id}";
   public final static String ID_PATH_VARIABLE = "id";

   // Constants used in project
   public final static String PROCESSING_NODE_NAME = "1";
   public final static String PROCESS_CATEGORY_MONITORING = "Monitoring";
   public final static String PROCESS_CATEGORY_DISPATCHER = "Dispatcher";
   public final static String PROCESS_CATEGORY_WORKER = "Worker";
}