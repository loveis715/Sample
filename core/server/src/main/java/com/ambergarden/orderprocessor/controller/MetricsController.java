package com.ambergarden.orderprocessor.controller;

import static com.ambergarden.orderprocessor.Constants.FIND_BY_ID_URL;
import static com.ambergarden.orderprocessor.Constants.ID_PATH_VARIABLE;
import static com.ambergarden.orderprocessor.Constants.METRICS_URL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ambergarden.orderprocessor.schema.beans.monitoring.ServerMetrics;
import com.ambergarden.orderprocessor.service.MetricsService;

/**
 * Controller for create and retrieve metrics for monitoring
 */
@Controller
@RequestMapping(value = METRICS_URL)
public class MetricsController {
   @Autowired
   private MetricsService metricsService;

   /**
    * List all metrics
    * @return a list of metrics
    */
   @RequestMapping(method = RequestMethod.GET)
   @ResponseBody
   public List<ServerMetrics> list() {
      return metricsService.findAll();
   }

   /**
    * Create/Update a metrics
    * @return the created/updated metrics
    */
   @RequestMapping(method = RequestMethod.PUT)
   @ResponseBody
   public ServerMetrics createOrUpdate(@PathVariable(ID_PATH_VARIABLE) String metricsId, @RequestBody ServerMetrics metrics) {
      return metricsService.save(metricsId, metrics);
   }

   /**
    * Find server metrics by specific id
    * @param id the id of the requested server metrics
    * @return the server metrics with that specific id
    */
   @RequestMapping(value = FIND_BY_ID_URL, method = RequestMethod.GET)
   @ResponseBody
   public ServerMetrics findById(@PathVariable(ID_PATH_VARIABLE) String id) {
      return metricsService.findById(id);
   }
}