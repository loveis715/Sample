package com.ambergarden.orderprocessor.orm.repository.monitoring;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ambergarden.orderprocessor.orm.entity.monitoring.ServerMetrics;

/**
 * Repository for performing CRUD operations on system metrics
 */
@Repository
public interface ServerMetricsRepository extends CrudRepository<ServerMetrics, String> {
}