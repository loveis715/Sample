package com.ambergarden.orderprocessor.monitoring;

import static com.ambergarden.orderprocessor.Constants.PROCESS_CATEGORY_MONITORING;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import com.ambergarden.orderprocessor.orm.entity.monitoring.ActiveProcess;
import com.ambergarden.orderprocessor.orm.repository.monitoring.ActiveProcessRepository;
import com.ambergarden.orderprocessor.schema.beans.monitoring.ServerMetrics;
import com.ambergarden.orderprocessor.service.MetricsService;

/**
 * Components used to monitor the states of server instances
 * in our monitoring system
 */
@Component
public class ServiceMonitor {
   @Autowired
   private MetricsService metricsService;

   @Autowired
   private ActiveProcessRepository activeProcessRepository;

   private String instanceId = "";

   public String getInstanceId() {
      return instanceId;
   }

   public void setInstanceId(String instanceId) {
      this.instanceId = instanceId;
   }

   public List<String> monitor() {
      // First update current process' metrics, to mark as alive
      ServerMetrics metrics = new ServerMetrics();
      metrics.setId(instanceId);
      metrics.setLastUpdateTime(new Date());
      metricsService.save(instanceId, metrics);

      // Check active monitoring process' state
      ActiveProcess activeProcess = activeProcessRepository.findOne(PROCESS_CATEGORY_MONITORING);
      if (activeProcess != null && !activeProcess.getProcessId().equals(instanceId)) {
         boolean isValid = checkServerInstanceState(activeProcess.getProcessId());
         if (!isValid) {
            makeCurrentActive();
         }
      } else if (activeProcess == null) {
         // We're starting. Try to grab the active role
         makeCurrentActive();
      }

      activeProcess = activeProcessRepository.findOne(PROCESS_CATEGORY_MONITORING);
      if (activeProcess.getProcessId().equals(instanceId)) {
         // We're the active monitoring process
         List<ServerMetrics> metricsList = metricsService.findAll();
         List<String> invalidProcesses = new ArrayList<String>();
         for (ServerMetrics serverMetrics : metricsList) {
            if (!checkServerInstanceState(serverMetrics)) {
               invalidProcesses.add(serverMetrics.getId());
            }
         }

         if (invalidProcesses.size() != 0) {
            sendEmail(invalidProcesses);
            return invalidProcesses;
         }
      }
      return new ArrayList<String>();
   }

   private void makeCurrentActive() {
      ActiveProcess activeProcess = activeProcessRepository.findOne(PROCESS_CATEGORY_MONITORING);
      if (activeProcess == null) {
         activeProcess = new ActiveProcess();
         activeProcess.setCategory(PROCESS_CATEGORY_MONITORING);
      }
      activeProcess.setProcessId(instanceId);

      try {
         activeProcessRepository.save(activeProcess);
      } catch (ObjectOptimisticLockingFailureException ex) {
         // Another process has grabbed the active role
         // This happends only when we're starting our system
      }
   }

   private boolean checkServerInstanceState(String processId) {
      ServerMetrics serverMetrics = metricsService.findById(processId);
      return checkServerInstanceState(serverMetrics);
   }

   // Assumes we're not only checking with the alive status, but also
   // CPU usage, memory usage, storage usage etc. The conditions should
   // be configurable for operation forks
   // And the return value should not be a simple boolean. It should
   // contain information about what has been in shortage etc.
   private boolean checkServerInstanceState(ServerMetrics metrics) {
      Date timestamp = new Date();
      timestamp = DateUtils.addMinutes(timestamp, -1);
      return metrics.getLastUpdateTime().after(timestamp);
   }

   // Fake method to indicate that we should send email or other
   // form of notifications to operators
   private void sendEmail(List<String> invalidProcesses) {
   }
}