package com.ambergarden.orderprocessor.orm.entity.monitoring;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 * Records the active monitoring process
 */
@Entity
public class ActiveProcess {
   @Id
   private String category;

   @Version
   protected int lockVersion;

   private String processId;

   public String getCategory() {
      return category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public int getLockVersion() {
      return lockVersion;
   }

   public void setLockVersion(int lockVersion) {
      this.lockVersion = lockVersion;
   }

   public String getProcessId() {
      return processId;
   }

   public void setProcessId(String processId) {
      this.processId = processId;
   }
}