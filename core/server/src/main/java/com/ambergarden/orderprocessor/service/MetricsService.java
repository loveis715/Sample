package com.ambergarden.orderprocessor.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ambergarden.orderprocessor.HomeController;
import com.ambergarden.orderprocessor.converter.ServerMetricsConverter;
import com.ambergarden.orderprocessor.exception.BadEntityRequestException;
import com.ambergarden.orderprocessor.exception.EntityNotFoundException;
import com.ambergarden.orderprocessor.orm.repository.monitoring.ServerMetricsRepository;
import com.ambergarden.orderprocessor.schema.beans.monitoring.ServerMetrics;

/**
 * Service for CRUD metrics
 */
@Service
public class MetricsService {
   private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

   @Autowired
   private ServerMetricsRepository serverMetricsRepository;

   @Autowired
   private ServerMetricsConverter serverMetricsConverter;

   /**
    * List all server metrics
    * @return all server metrics
    */
   public List<ServerMetrics> findAll() {
      // An audit log should also contain which performs this action
      // Since we have not provide any AuthN service, so just add an operation log
      logger.info("MetricsService - findAll() invoked");

      return serverMetricsConverter.convertListFrom(serverMetricsRepository.findAll());
   }

   /**
    * Create or update a server metrics
    * Force to use metricsId parameter to follow the REST style
    * @param metrics the server metrics to save
    * @return the persisted server metrics
    */
   public ServerMetrics save(String metricsId, ServerMetrics metrics) {
      logger.info("MetricsService - save() invoked");
      validateInputForSave(metricsId, metrics);

      com.ambergarden.orderprocessor.orm.entity.monitoring.ServerMetrics metricsMO
         = serverMetricsConverter.convertTo(metrics);
      metricsMO = serverMetricsRepository.save(metricsMO);
      return serverMetricsConverter.convertFrom(metricsMO);
   }

   /**
    * Find the server metrics with specific id
    * @param metricsId id the id of the requested server metrics
    * @return the server metrics with that specific id
    */
   public ServerMetrics findById(String metricsId) {
      logger.info("MetricsService - findById() invoked");
      if (StringUtils.isBlank(metricsId)) {
         throw new BadEntityRequestException();
      }

      com.ambergarden.orderprocessor.orm.entity.monitoring.ServerMetrics metricsMO
         = serverMetricsRepository.findOne(metricsId);
      if (metricsMO == null) {
         throw new EntityNotFoundException();
      }

      return serverMetricsConverter.convertFrom(metricsMO);
   }

   private void validateInputForSave(String metricsId, ServerMetrics metrics) {
      // Metrics id should not be null, "" and " " etc.
      if (StringUtils.isBlank(metricsId)) {
         throw new BadEntityRequestException();
      }

      // Inconsistent metrics id
      if (!metricsId.equals(metrics.getId())) {
         throw new BadEntityRequestException();
      }

      // Last update time is the "required field"
      if (metrics.getLastUpdateTime() == null) {
         throw new BadEntityRequestException();
      }
   }
}