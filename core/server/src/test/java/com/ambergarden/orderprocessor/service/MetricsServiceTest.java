package com.ambergarden.orderprocessor.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.orderprocessor.exception.BadEntityRequestException;
import com.ambergarden.orderprocessor.exception.EntityNotFoundException;
import com.ambergarden.orderprocessor.orm.repository.monitoring.ServerMetricsRepository;
import com.ambergarden.orderprocessor.schema.beans.monitoring.ServerMetrics;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/spring/test-context.xml" })
public class MetricsServiceTest {
   private static String MOCK_SERVER_NODE = "mockNode";
   private static String MOCK_SERVER_NODE_2 = "mockNode2";
   private static String INVALID_ID = "invalidId";
   private static String BLANK_ID = " ";
   private static String EMPTY_STRING = "";

   private static double MOCK_CPU_USAGE = 0.1;
   private static double MOCK_MEMORY_USAGE = 0.2;
   private static double MOCK_STORAGE_USAGE = 0.3;

   @Autowired
   private MetricsService metricsService;

   @Autowired
   private ServerMetricsRepository serverMetricsRepository;

   @Before
   public void init() {
      // If the node has already exists, delete it first
      com.ambergarden.orderprocessor.orm.entity.monitoring.ServerMetrics metrics = serverMetricsRepository.findOne(MOCK_SERVER_NODE);
      if (metrics != null) {
         serverMetricsRepository.delete(MOCK_SERVER_NODE);
      }
   }

   @Test
   public void testServerMetricsCRUD() {
      List<ServerMetrics> result = metricsService.findAll();
      int resultCount = result.size();

      // Create a new metrics will increase the result count
      ServerMetrics mockMetrics = createMockMetrics();
      metricsService.save(MOCK_SERVER_NODE, mockMetrics);

      // Verify that the server metrics has been created
      mockMetrics = metricsService.findById(MOCK_SERVER_NODE);
      assertNotNull(mockMetrics);

      // Verify that the server metrics count has been increased
      result = metricsService.findAll();
      assertEquals(resultCount + 1, result.size());

      // Update that metrics will not increase the result count
      mockMetrics.setLastUpdateTime(new Date());
      metricsService.save(MOCK_SERVER_NODE, mockMetrics);

      // Verify that update does not create extra entry
      result = metricsService.findAll();
      assertEquals(resultCount + 1, result.size());
   }

   @Test(expected = EntityNotFoundException.class)
   public void testFindWithInvalidId() {
      metricsService.findById(INVALID_ID);
   }

   @Test(expected = BadEntityRequestException.class)
   public void testFindWithIdNull() {
      metricsService.findById(null);
   }

   @Test(expected = BadEntityRequestException.class)
   public void testFindWithEmptyId() {
      metricsService.findById(EMPTY_STRING);
   }

   @Test(expected = BadEntityRequestException.class)
   public void testFindWithBlankId() {
      metricsService.findById(BLANK_ID);
   }

   @Test(expected = BadEntityRequestException.class)
   public void testSaveWithIdNull() {
      ServerMetrics metrics = createMockMetrics();
      metricsService.save(null, metrics);
   }

   @Test(expected = BadEntityRequestException.class)
   public void testCreateWithInconsistentId() {
      ServerMetrics metrics = createMockMetrics();
      metricsService.save(MOCK_SERVER_NODE_2, metrics);
   }

   @Test(expected = BadEntityRequestException.class)
   public void testSaveWithEmptyId() {
      ServerMetrics metrics = createMockMetrics();
      metricsService.save(EMPTY_STRING, metrics);
   }

   @Test(expected = BadEntityRequestException.class)
   public void testSaveWithBlankId() {
      ServerMetrics metrics = createMockMetrics();
      metricsService.save(BLANK_ID, metrics);
   }

   @Test(expected = BadEntityRequestException.class)
   public void testSaveWithoutLastUpdate() {
      ServerMetrics metrics = createMockMetrics();
      metrics.setLastUpdateTime(null);
      metricsService.save(MOCK_SERVER_NODE, metrics);
   }

   private ServerMetrics createMockMetrics() {
      Date timestamp = new Date();
      ServerMetrics metrics = new ServerMetrics();
      metrics.setId(MOCK_SERVER_NODE);
      metrics.setLastUpdateTime(timestamp);
      metrics.setCpuUsage(MOCK_CPU_USAGE);
      metrics.setMemoryUsage(MOCK_MEMORY_USAGE);
      metrics.setStorageUsage(MOCK_STORAGE_USAGE);
      return metrics;
   }
}