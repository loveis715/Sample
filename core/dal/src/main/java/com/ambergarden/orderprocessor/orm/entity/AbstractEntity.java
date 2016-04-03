package com.ambergarden.orderprocessor.orm.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Abstract entity which holds entity's id, create time and last
 * update time. All persisted entity should extend this class.
 */
@MappedSuperclass
public abstract class AbstractEntity {

   @Id
   @GeneratedValue(strategy=GenerationType.AUTO)
   private int id = -1;

   private Date startTime;

   private Date lastUpdateTime;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public Date getStartTime() {
      return startTime;
   }

   public void setStartTime(Date startTime) {
      this.startTime = startTime;
   }

   public Date getLastUpdateTime() {
      return lastUpdateTime;
   }

   public void setLastUpdateTime(Date lastUpdateTime) {
      this.lastUpdateTime = lastUpdateTime;
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder()
         .append(this.getId())
         .append(this.getClass())
         .toHashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }

      AbstractVersionedEntity entity = (AbstractVersionedEntity) obj;
      return new EqualsBuilder()
         .append(this.getId(), entity.getId())
         .isEquals();
   }
}