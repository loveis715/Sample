package com.ambergarden.orderprocessor.orm.repository.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.orderprocessor.orm.entity.monitoring.ServerMetrics;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/META-INF/spring/db/dal-*-context.xml" })
public class ServerMetricsRepositoryTest {
   private static String MOCK_SERVER_NODE = "mockNode";
   private static double MOCK_CPU_USAGE = 0.1;
   private static double MOCK_MEMORY_USAGE = 0.2;
   private static double MOCK_STORAGE_USAGE = 0.3;
   private static double DELTA = 0.000001;

   @Autowired
   private ServerMetricsRepository serverMetricsRepository;

   @Test
   public void testServerMetricsCRUD() {
      // Save and verify the result
      ServerMetrics metrics = createMockMetrics();
      Date timestamp = metrics.getLastUpdateTime();
      metrics = serverMetricsRepository.save(metrics);

      assertEquals(MOCK_SERVER_NODE, metrics.getId());
      assertEquals(timestamp, metrics.getLastUpdateTime());
      assertTrue(metrics.getCPUUsage() > MOCK_CPU_USAGE - DELTA && metrics.getCPUUsage() < MOCK_CPU_USAGE + DELTA);
      assertTrue(metrics.getMemoryUsage() > MOCK_MEMORY_USAGE - DELTA && metrics.getMemoryUsage() < MOCK_MEMORY_USAGE + DELTA);
      assertTrue(metrics.getStorageUsage() > MOCK_STORAGE_USAGE - DELTA && metrics.getStorageUsage() < MOCK_STORAGE_USAGE + DELTA);

      // Update and verify
      timestamp = new Date();
      metrics.setLastUpdateTime(timestamp);
      metrics = serverMetricsRepository.save(metrics);
      assertEquals(timestamp, metrics.getLastUpdateTime());
   }

   @Test(expected = JpaSystemException.class)
   public void testCreateMetricsWithoutId() {
      ServerMetrics metrics = createMockMetrics();
      metrics.setId(null);
      serverMetricsRepository.save(metrics);
   }

   @Test(expected = DataIntegrityViolationException.class)
   public void testCreateMetricsWithoutUpdateTime() {
      ServerMetrics metrics = createMockMetrics();
      metrics.setLastUpdateTime(null);
      serverMetricsRepository.save(metrics);
   }

   private ServerMetrics createMockMetrics() {
      Date timestamp = new Date();
      ServerMetrics metrics = new ServerMetrics();
      metrics.setId(MOCK_SERVER_NODE);
      metrics.setLastUpdateTime(timestamp);
      metrics.setCPUUsage(MOCK_CPU_USAGE);
      metrics.setMemoryUsage(MOCK_MEMORY_USAGE);
      metrics.setStorageUsage(MOCK_STORAGE_USAGE);
      return metrics;
   }
}