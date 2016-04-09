package com.ambergarden.orderprocessor.converter;

import org.springframework.stereotype.Component;

import com.ambergarden.orderprocessor.orm.entity.monitoring.ServerMetrics;

/**
 * Converts between server metrics' model object and data transfer object
 */
@Component
public class ServerMetricsConverter
   extends AbstractEntityListConverter<ServerMetrics, com.ambergarden.orderprocessor.schema.beans.monitoring.ServerMetrics> {

   @Override
   public com.ambergarden.orderprocessor.schema.beans.monitoring.ServerMetrics convertFrom(ServerMetrics mo) {
      com.ambergarden.orderprocessor.schema.beans.monitoring.ServerMetrics metrics
         = new com.ambergarden.orderprocessor.schema.beans.monitoring.ServerMetrics();
      metrics.setId(mo.getId());
      metrics.setLastUpdateTime(mo.getLastUpdateTime());
      metrics.setCpuUsage(mo.getCPUUsage());
      metrics.setMemoryUsage(mo.getMemoryUsage());
      metrics.setStorageUsage(mo.getStorageUsage());
      return metrics;
   }

   @Override
   public ServerMetrics convertTo(com.ambergarden.orderprocessor.schema.beans.monitoring.ServerMetrics dto) {
      ServerMetrics metrics = new ServerMetrics();
      metrics.setId(dto.getId());
      metrics.setLastUpdateTime(dto.getLastUpdateTime());
      metrics.setCPUUsage(dto.getCpuUsage());
      metrics.setMemoryUsage(dto.getMemoryUsage());
      metrics.setStorageUsage(dto.getStorageUsage());
      return metrics;
   }
}