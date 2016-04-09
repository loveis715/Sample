package com.ambergarden.orderprocessor.orm.repository.monitoring;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ambergarden.orderprocessor.orm.entity.monitoring.ActiveProcess;

/**
 * Repository for performing CRUD operations on active process records
 */
@Repository
public interface ActiveProcessRepository extends CrudRepository<ActiveProcess, String> {
}