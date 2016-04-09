package com.ambergarden.orderprocessor.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.orderprocessor.Constants;
import com.ambergarden.orderprocessor.orm.entity.monitoring.ActiveProcess;
import com.ambergarden.orderprocessor.orm.entity.monitoring.ServerMetrics;
import com.ambergarden.orderprocessor.orm.repository.monitoring.ActiveProcessRepository;
import com.ambergarden.orderprocessor.orm.repository.monitoring.ServerMetricsRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/spring/monitoring-test-context.xml" })
public class ServiceMonitorTest {
   private static final String MONITOR_PROCESS_NAME_1 = "mockMonitor1";
   private static final String MONITOR_PROCESS_NAME_2 = "mockMonitor2";
   private static final String WORKER_PROCESS_NAME_1 = "mockWorker1";
   private static final String WORKER_PROCESS_NAME_2 = "mockWorker2";

   @Autowired
   private ServiceMonitor serviceMonitor;

   @Autowired
   private ServerMetricsRepository metricsRepository;

   @Autowired
   private ActiveProcessRepository activeProcessRepository;

   @Before
   public void init() {
      // Clear environment. The database always contains dirty
      // data, e.g. in-complete test cases
      metricsRepository.deleteAll();
      activeProcessRepository.deleteAll();
   }

   @Test
   public void testActiveMonitorProcess() {
      // Construct a healthy environment
      Date timestamp = new Date();
      createHealthyProcess(MONITOR_PROCESS_NAME_1, timestamp);
      createHealthyProcess(MONITOR_PROCESS_NAME_2, timestamp);
      createHealthyProcess(WORKER_PROCESS_NAME_1, timestamp);
      createHealthyProcess(WORKER_PROCESS_NAME_2, timestamp);
      setActiveProcess(MONITOR_PROCESS_NAME_1);

      serviceMonitor.setInstanceId(MONITOR_PROCESS_NAME_1);
      List<String> invalidProcesses = serviceMonitor.monitor();
      assertEquals(0, invalidProcesses.size());

      ActiveProcess activeProcess = activeProcessRepository.findOne(Constants.PROCESS_CATEGORY_MONITORING);
      assertNotNull(activeProcess);
      assertEquals(MONITOR_PROCESS_NAME_1, activeProcess.getProcessId());
   }

   @Test
   public void testPassiveMonitorProcess() {
      // Construct a healthy environment
      Date timestamp = new Date();
      createHealthyProcess(MONITOR_PROCESS_NAME_1, timestamp);
      createHealthyProcess(MONITOR_PROCESS_NAME_2, timestamp);
      createHealthyProcess(WORKER_PROCESS_NAME_1, timestamp);
      createHealthyProcess(WORKER_PROCESS_NAME_2, timestamp);
      setActiveProcess(MONITOR_PROCESS_NAME_1);

      serviceMonitor.setInstanceId(MONITOR_PROCESS_NAME_2);
      List<String> invalidProcesses = serviceMonitor.monitor();
      assertEquals(0, invalidProcesses.size());

      ActiveProcess activeProcess = activeProcessRepository.findOne(Constants.PROCESS_CATEGORY_MONITORING);
      assertNotNull(activeProcess);
      assertEquals(MONITOR_PROCESS_NAME_1, activeProcess.getProcessId());
   }

   @Test
   public void testSwitchMonitorProcess() {
      // Construct a environment in which the monitoring proess has not
      // been active for more than one minutes
      Date timestamp = new Date();
      createHealthyProcess(MONITOR_PROCESS_NAME_1, DateUtils.addMinutes(timestamp, -2));
      createHealthyProcess(MONITOR_PROCESS_NAME_2, timestamp);
      createHealthyProcess(WORKER_PROCESS_NAME_1, timestamp);
      createHealthyProcess(WORKER_PROCESS_NAME_2, timestamp);
      setActiveProcess(MONITOR_PROCESS_NAME_1);

      serviceMonitor.setInstanceId(MONITOR_PROCESS_NAME_2);
      List<String> invalidProcesses = serviceMonitor.monitor();
      assertEquals(1, invalidProcesses.size());

      ActiveProcess activeProcess = activeProcessRepository.findOne(Constants.PROCESS_CATEGORY_MONITORING);
      assertNotNull(activeProcess);
      assertEquals(MONITOR_PROCESS_NAME_2, activeProcess.getProcessId());
   }

   @Test
   public void testWithWorkerProcessFailure() {
      // Construct a environment in which a worker process has been down
      Date timestamp = new Date();
      createHealthyProcess(MONITOR_PROCESS_NAME_1, timestamp);
      createHealthyProcess(MONITOR_PROCESS_NAME_2, timestamp);
      createHealthyProcess(WORKER_PROCESS_NAME_1, DateUtils.addMinutes(timestamp, -2));
      createHealthyProcess(WORKER_PROCESS_NAME_2, timestamp);
      setActiveProcess(MONITOR_PROCESS_NAME_1);

      serviceMonitor.setInstanceId(MONITOR_PROCESS_NAME_1);
      List<String> invalidProcesses = serviceMonitor.monitor();
      assertEquals(1, invalidProcesses.size());

      ActiveProcess activeProcess = activeProcessRepository.findOne(Constants.PROCESS_CATEGORY_MONITORING);
      assertNotNull(activeProcess);
      assertEquals(MONITOR_PROCESS_NAME_1, activeProcess.getProcessId());
   }

   private void createHealthyProcess(String processId, Date timestamp) {
      ServerMetrics metrics = new ServerMetrics();
      metrics.setId(processId);
      metrics.setLastUpdateTime(timestamp);
      metricsRepository.save(metrics);
   }

   private void setActiveProcess(String processId) {
      ActiveProcess activeProcess = activeProcessRepository.findOne(Constants.PROCESS_CATEGORY_MONITORING);
      if (activeProcess == null) {
         activeProcess = new ActiveProcess();
         activeProcess.setCategory(Constants.PROCESS_CATEGORY_MONITORING);
      }
      activeProcess.setProcessId(processId);
      activeProcessRepository.save(activeProcess);
   }
}