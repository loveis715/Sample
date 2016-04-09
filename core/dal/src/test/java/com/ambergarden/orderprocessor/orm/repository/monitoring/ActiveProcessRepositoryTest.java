package com.ambergarden.orderprocessor.orm.repository.monitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ambergarden.orderprocessor.orm.entity.monitoring.ActiveProcess;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/META-INF/spring/db/dal-*-context.xml" })
public class ActiveProcessRepositoryTest {
   private static final String MOCK_PROCESS_CATEGORY = "mockCategory";
   private static final String MOCK_PROCESS_NAME_1 = "mockName1";
   private static final String MOCK_PROCESS_NAME_2 = "mockName2";

   @Autowired
   private ActiveProcessRepository activeProcessRepository;

   @Before
   public void init() {
      ActiveProcess activeProcess = activeProcessRepository.findOne(MOCK_PROCESS_CATEGORY);
      if (activeProcess != null) {
         activeProcessRepository.delete(MOCK_PROCESS_CATEGORY);
      }
   }

   @Test
   public void testCRUD() {
      // Test create
      ActiveProcess activeProcess = new ActiveProcess();
      activeProcess.setCategory(MOCK_PROCESS_CATEGORY);
      activeProcess.setProcessId(MOCK_PROCESS_NAME_1);
      activeProcess = activeProcessRepository.save(activeProcess);
      assertNotNull(activeProcess);
      assertEquals(MOCK_PROCESS_CATEGORY, activeProcess.getCategory());
      assertEquals(MOCK_PROCESS_NAME_1, activeProcess.getProcessId());

      // Test update
      activeProcess.setProcessId(MOCK_PROCESS_NAME_2);
      activeProcess = activeProcessRepository.save(activeProcess);
      assertNotNull(activeProcess);
      assertEquals(MOCK_PROCESS_CATEGORY, activeProcess.getCategory());
      assertEquals(MOCK_PROCESS_NAME_2, activeProcess.getProcessId());
   }

   @Test(expected = JpaSystemException.class)
   public void testCreateWithIdNull() {
      ActiveProcess activeProcess = new ActiveProcess();
      activeProcess.setProcessId(MOCK_PROCESS_NAME_1);
      activeProcessRepository.save(activeProcess);
   }

   @Test(expected = ObjectOptimisticLockingFailureException.class)
   public void testOptimisticLock() {
      // Create a new order and save it
      ActiveProcess activeProcess = new ActiveProcess();
      activeProcess.setCategory(MOCK_PROCESS_CATEGORY);
      activeProcess.setProcessId(MOCK_PROCESS_NAME_1);
      activeProcess = activeProcessRepository.save(activeProcess);
      assertEquals(0, activeProcess.getLockVersion());

      // Update & save that order again, the version should be increased by 1
      activeProcess.setProcessId(MOCK_PROCESS_NAME_2);
      activeProcess = activeProcessRepository.save(activeProcess);
      assertEquals(1, activeProcess.getLockVersion());

      // Using a previous version should throw an ObjectOptimisticLockingFailureException
      activeProcess.setLockVersion(0);
      activeProcessRepository.save(activeProcess);
   }
}