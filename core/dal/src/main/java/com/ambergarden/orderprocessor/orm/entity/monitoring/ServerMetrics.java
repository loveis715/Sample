package com.ambergarden.orderprocessor.orm.entity.monitoring;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Represents the server instance's metrics
 */
@Entity
public class ServerMetrics {
   @Id
   private String id;

   private Date lastUpdateTime;

   @Column(name="cpuUsage", columnDefinition="numeric(5, 2)")
   private double cpuUsage;

   @Column(name="memoryUsage", columnDefinition="numeric(5, 2)")
   private double memoryUsage;

   @Column(name="storageUsage", columnDefinition="numeric(5, 2)")
   private double storageUsage;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Date getLastUpdateTime() {
      return lastUpdateTime;
   }

   public void setLastUpdateTime(Date lastUpdateTime) {
      this.lastUpdateTime = lastUpdateTime;
   }

   public double getCPUUsage() {
      return cpuUsage;
   }

   public void setCPUUsage(double cpuUsage) {
      this.cpuUsage = cpuUsage;
   }

   public double getMemoryUsage() {
      return memoryUsage;
   }

   public void setMemoryUsage(double memoryUsage) {
      this.memoryUsage = memoryUsage;
   }

   public double getStorageUsage() {
      return storageUsage;
   }

   public void setStorageUsage(double storageUsage) {
      this.storageUsage = storageUsage;
   }
}