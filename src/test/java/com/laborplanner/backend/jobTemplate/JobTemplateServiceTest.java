package com.laborplanner.backend.jobTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.laborplanner.backend.exception.job.DuplicateJobTemplateNameException;
import com.laborplanner.backend.exception.job.JobTemplateNotFoundException;
import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.model.User;
import com.laborplanner.backend.repository.JobTemplateRepository;
import com.laborplanner.backend.service.JobTemplateService;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class JobTemplateServiceTest {

   @Mock
   private JobTemplateRepository jobTemplateRepository;

   @InjectMocks
   private JobTemplateService jobTemplateService;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
   }

   // ----------------------------
   // Get and list tests
   // ----------------------------

   @Test
   void getAllJobTemplates_returnsList() {
      JobTemplate t1 = new JobTemplate();
      t1.setJobTemplateUuid("t1");
      t1.setName("A");

      JobTemplate t2 = new JobTemplate();
      t2.setJobTemplateUuid("t2");
      t2.setName("B");

      when(jobTemplateRepository.findAllByOrderByNameAsc()).thenReturn(List.of(t1, t2));

      List<JobTemplate> all = jobTemplateService.getAllJobTemplates();

      assertEquals(2, all.size());
      assertEquals("A", all.get(0).getName());
   }

   @Test
   void getJobTemplateByUuid_whenExists_returnsTemplate() {
      JobTemplate t = new JobTemplate();
      t.setJobTemplateUuid("id");
      t.setName("Test");

      when(jobTemplateRepository.findByUuid("id")).thenReturn(Optional.of(t));

      JobTemplate result = jobTemplateService.getJobTemplateByUuid("id");

      assertEquals("id", result.getJobTemplateUuid());
      assertEquals("Test", result.getName());
   }

   @Test
   void getJobTemplateByUuid_whenMissing_throwsException() {
      when(jobTemplateRepository.findByUuid("missing")).thenReturn(Optional.empty());
      assertThrows(JobTemplateNotFoundException.class, () -> jobTemplateService.getJobTemplateByUuid("missing"));
   }

   // ----------------------------
   // Find by (machineType, user, name)
   // ----------------------------

   @Test
   void findByName_returnsOptional() {
      JobTemplate t = new JobTemplate();
      t.setName("Foo");

      when(jobTemplateRepository.findByName("Foo")).thenReturn(Optional.of(t));

      Optional<JobTemplate> result = jobTemplateService.findByName("Foo");

      assertTrue(result.isPresent());
      assertEquals("Foo", result.get().getName());
   }

   @Test
   void findByRequiredMachineType_returnsList() {
      MachineType m = new MachineType();
      m.setMachineTypeUuid("m1");

      JobTemplate t1 = new JobTemplate();
      t1.setName("A");

      JobTemplate t2 = new JobTemplate();
      t2.setName("B");

      when(jobTemplateRepository.findByRequiredMachineType(m)).thenReturn(List.of(t1, t2));

      List<JobTemplate> results = jobTemplateService.findByRequiredMachineType(m);

      assertEquals(2, results.size());
   }

   @Test
   void findByCreatedByUser_returnsList() {
      User user = new User();
      user.setUserUuid("u1");

      JobTemplate t = new JobTemplate();
      t.setName("MadeByUser");

      when(jobTemplateRepository.findByCreatedByUser(user)).thenReturn(List.of(t));

      List<JobTemplate> results = jobTemplateService.findByCreatedByUser(user);

      assertEquals(1, results.size());
      assertEquals("MadeByUser", results.get(0).getName());
   }

   // ----------------------------
   // Create tests
   // ----------------------------

   @Test
   void createJobTemplate_duplicateName_throwsException() {
      JobTemplate t = new JobTemplate();
      t.setName("Dup");

      when(jobTemplateRepository.existsByName("Dup")).thenReturn(true);

      assertThrows(DuplicateJobTemplateNameException.class, () -> jobTemplateService.createJobTemplate(t));
      verify(jobTemplateRepository, never()).create(any());
   }

   @Test
   void createJobTemplate_success_creates() {
      JobTemplate t = new JobTemplate();
      t.setName("Good");

      when(jobTemplateRepository.existsByName("Good")).thenReturn(false);
      when(jobTemplateRepository.create(any(JobTemplate.class)))
            .thenAnswer(i -> {
               JobTemplate j = i.getArgument(0);
               j.setJobTemplateUuid("uuid-11");
               return j;
            });

      JobTemplate created = jobTemplateService.createJobTemplate(t);

      assertEquals("uuid-11", created.getJobTemplateUuid());
      assertEquals("Good", created.getName());
   }

   // ----------------------------
   // Update tests
   // ----------------------------

   @Test
   void updateJobTemplate_whenExists_updatesSuccessfully() {
      MachineType m = new MachineType();
      m.setMachineTypeUuid("m1");
      User u = new User();
      u.setUserUuid("u1");

      JobTemplate existing = new JobTemplate();
      existing.setJobTemplateUuid("id");
      existing.setName("Old");
      existing.setDescription("OldDesc");
      existing.setDuration(Duration.ofMinutes(10));
      existing.setRequiredMachineType(m);
      existing.setCreatedByUser(u);

      JobTemplate updated = new JobTemplate();
      updated.setName("New");
      updated.setDescription("NewDesc");
      updated.setDuration(Duration.ofMinutes(20));
      updated.setRequiredMachineType(m);
      updated.setCreatedByUser(u);

      when(jobTemplateRepository.findByUuid("id")).thenReturn(Optional.of(existing));
      when(jobTemplateRepository.create(any(JobTemplate.class)))
            .thenAnswer(i -> i.getArgument(0));

      JobTemplate saved = jobTemplateService.updateJobTemplate("id", updated);

      assertEquals("New", saved.getName());
      assertEquals("NewDesc", saved.getDescription());
      assertEquals(Duration.ofMinutes(20), saved.getDuration());
   }

   @Test
   void updateJobTemplate_whenMissing_throwsException() {
      JobTemplate updated = new JobTemplate();
      updated.setName("X");

      when(jobTemplateRepository.findByUuid("missing")).thenReturn(Optional.empty());

      assertThrows(JobTemplateNotFoundException.class, () -> jobTemplateService.updateJobTemplate("missing", updated));
   }

   // ----------------------------
   // Delete tests
   // ----------------------------

   @Test
   void deleteJobTemplate_whenExists_deletesSuccessfully() {
      when(jobTemplateRepository.existsByUuid("del")).thenReturn(true);
      doNothing().when(jobTemplateRepository).deleteByUuid("del");

      jobTemplateService.deleteJobTemplate("del");

      verify(jobTemplateRepository, times(1)).deleteByUuid("del");
   }

   @Test
   void deleteJobTemplate_whenMissing_throwsException() {
      when(jobTemplateRepository.existsByUuid("nope")).thenReturn(false);

      assertThrows(JobTemplateNotFoundException.class, () -> jobTemplateService.deleteJobTemplate("nope"));
      verify(jobTemplateRepository, never()).deleteByUuid(any());
   }
}
