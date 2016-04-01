package com.ambergarden.orderprocessor.orm.entity;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Abstract entity which holds entity's id and version. Derived classes
 * should use version as optimistic lock.
 */
@MappedSuperclass
public abstract class AbstractVersionedEntity extends AbstractEntity {
   @Version
   protected int lockVersion;

   public int getLockVersion() {
      return lockVersion;
   }

   public void setLockVersion(int lockVersion) {
      this.lockVersion = lockVersion;
   }
}