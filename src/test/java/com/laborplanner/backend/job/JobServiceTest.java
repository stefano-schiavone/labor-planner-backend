package com.laborplanner.backend.job;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.laborplanner.backend.dto.job.JobDto;
import com.laborplanner.backend.exception.job.DuplicateJobNameException;
import com.laborplanner.backend.exception.job.JobNotFoundException;
import com.laborplanner.backend.exception.machine.MachineTypeNotFoundException;
import com.laborplanner.backend.exception.scheduledJob.ScheduledJobConflictException;
import com.laborplanner.backend.model.Job;
import com.laborplanner.backend.model.JobTemplate;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.model.Schedule;
import com.laborplanner.backend.repository.JobRepository;
import com.laborplanner.backend.repository.ScheduleRepository;
import com.laborplanner.backend.service.JobService;
import com.laborplanner.backend.service.interfaces.IJobTemplateReadService;
import com.laborplanner.backend.service.interfaces.IMachineTypeReadService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class JobServiceTest {

   @Mock
   private JobRepository jobRepository;

   @Mock
   private IMachineTypeReadService machineTypeService;

   @Mock
   private ScheduleRepository scheduleRepository;

   @Mock
   private IJobTemplateReadService jobTemplateService;

   @InjectMocks
   private JobService jobService;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
   }

   // ----------------------------
   // Create job tests
   // ----------------------------

   @Test
   void createJob_withDuplicateName_throwsException() {
      JobDto dto = new JobDto(
            null, // jobUuid
            null, // templateUuid
            "Duplicate Job",
            "Desc",
            30,
            LocalDateTime.now().plusDays(1),
            "machine-uuid");

      when(jobRepository.existsByName("Duplicate Job")).thenReturn(true);

      assertThrows(DuplicateJobNameException.class, () -> jobService.createJob(dto));
      verify(jobRepository, times(1)).existsByName("Duplicate Job");
      verifyNoMoreInteractions(jobRepository);
      verifyNoInteractions(machineTypeService, jobTemplateService);
   }

   @Test
   void createJob_withValidData_createsJob_withoutTemplate() {
      JobDto dto = new JobDto(
            null,
            null, // no template
            "Valid Job",
            "Desc",
            30,
            LocalDateTime.now().plusDays(1),
            "machine-uuid");

      MachineType machineType = new MachineType();
      machineType.setMachineTypeUuid("machine-uuid");

      when(jobRepository.existsByName("Valid Job")).thenReturn(false);
      when(machineTypeService.getTypeByUuid("machine-uuid")).thenReturn(machineType);
      when(jobRepository.create(any(Job.class)))
            .thenAnswer(
                  i -> {
                     Job j = i.getArgument(0);
                     j.setJobUuid("job-uuid-123");
                     return j;
                  });

      JobDto created = jobService.createJob(dto);

      assertNotNull(created);
      assertEquals("job-uuid-123", created.getJobUuid());
      assertEquals("Valid Job", created.getName());
      assertNull(created.getTemplateUuid());
      verify(jobTemplateService, never()).getJobTemplateByUuid(any());
   }

   @Test
   void createJob_withTemplate_createsJob_and_setsTemplateUuid() {
      JobDto dto = new JobDto(
            null,
            "template-uuid-1", // template provided
            "Templated Job",
            "Desc",
            45,
            LocalDateTime.now().plusDays(2),
            "machine-uuid");

      MachineType machineType = new MachineType();
      machineType.setMachineTypeUuid("machine-uuid");

      JobTemplate template = new JobTemplate();
      template.setJobTemplateUuid("template-uuid-1");

      when(jobRepository.existsByName("Templated Job")).thenReturn(false);
      when(machineTypeService.getTypeByUuid("machine-uuid")).thenReturn(machineType);
      when(jobTemplateService.getJobTemplateByUuid("template-uuid-1")).thenReturn(template);
      when(jobRepository.create(any(Job.class)))
            .thenAnswer(
                  i -> {
                     Job j = i.getArgument(0);
                     j.setJobUuid("job-with-template-uuid");
                     return j;
                  });

      JobDto created = jobService.createJob(dto);

      assertNotNull(created);
      assertEquals("job-with-template-uuid", created.getJobUuid());
      assertEquals("Templated Job", created.getName());
      assertEquals("template-uuid-1", created.getTemplateUuid());
      verify(jobTemplateService, times(1)).getJobTemplateByUuid("template-uuid-1");
   }

   @Test
   void createJob_withMissingMachineType_throwsMachineTypeNotFound() {
      JobDto dto = new JobDto(
            null,
            null,
            "Job With Missing Machine",
            "Desc",
            30,
            LocalDateTime.now().plusDays(1),
            "invalid-uuid");

      when(jobRepository.existsByName("Job With Missing Machine")).thenReturn(false);
      when(machineTypeService.getTypeByUuid("invalid-uuid"))
            .thenThrow(new MachineTypeNotFoundException("invalid-uuid"));

      assertThrows(MachineTypeNotFoundException.class, () -> jobService.createJob(dto));
      verify(machineTypeService, times(1)).getTypeByUuid("invalid-uuid");
      verify(jobRepository, never()).create(any());
   }

   // ----------------------------
   // Get and list tests
   // ----------------------------

   @Test
   void getAllJobs_returnsMappedDtos() {
      MachineType m1 = new MachineType();
      m1.setMachineTypeUuid("m1");

      MachineType m2 = new MachineType();
      m2.setMachineTypeUuid("m2");

      Job tJob1 = new Job("A", "d1", 10, LocalDateTime.now().plusDays(1), m1, null);
      tJob1.setJobUuid("j1");
      JobTemplate tmpl = new JobTemplate();
      tmpl.setJobTemplateUuid("tmpl-1");
      Job tJob2 = new Job("B", "d2", 20, LocalDateTime.now().plusDays(2), m2, tmpl);
      tJob2.setJobUuid("j2");

      when(jobRepository.findAllByOrderByDeadlineAsc()).thenReturn(List.of(tJob1, tJob2));

      List<JobDto> all = jobService.getAllJobs();

      assertEquals(2, all.size());
      JobDto dto1 = all.get(0);
      JobDto dto2 = all.get(1);

      assertEquals("j1", dto1.getJobUuid());
      assertEquals("m1", dto1.getRequiredMachineTypeUuid());
      assertNull(dto1.getTemplateUuid());

      assertEquals("j2", dto2.getJobUuid());
      assertEquals("m2", dto2.getRequiredMachineTypeUuid());
      assertEquals("tmpl-1", dto2.getTemplateUuid());
   }

   @Test
   void getJobByUuid_whenExists_returnsDto() {
      MachineType m = new MachineType();
      m.setMachineTypeUuid("m1");
      Job job = new Job("Name", "Desc", 15, LocalDateTime.now().plusDays(3), m, null);
      job.setJobUuid("job-123");

      when(jobRepository.findByUuid("job-123")).thenReturn(Optional.of(job));

      JobDto dto = jobService.getJobByUuid("job-123");

      assertEquals("job-123", dto.getJobUuid());
      assertEquals("Name", dto.getName());
   }

   @Test
   void getJobByUuid_whenMissing_throwsJobNotFound() {
      when(jobRepository.findByUuid("missing")).thenReturn(Optional.empty());
      assertThrows(JobNotFoundException.class, () -> jobService.getJobByUuid("missing"));
   }

   // ----------------------------
   // Update tests
   // ----------------------------

   @Test
   void updateJob_successful_update_returnsUpdatedDto() {
      // existing job in repo
      MachineType existingType = new MachineType();
      existingType.setMachineTypeUuid("old-m");
      Job existing = new Job(
            "Old Name",
            "oldDesc",
            10,
            LocalDateTime.now().plusDays(1),
            existingType,
            null);
      existing.setJobUuid("job-1");

      // DTO with new values and different machine type
      JobDto dto = new JobDto(
            "job-1", // jobUuid (not used for lookup)
            null,
            "New Name",
            "New Desc",
            30,
            LocalDateTime.now().plusDays(5),
            "new-m");

      MachineType newType = new MachineType();
      newType.setMachineTypeUuid("new-m");

      when(jobRepository.findByUuid("job-1")).thenReturn(Optional.of(existing));
      // name changed -> check existsByName should return false (no duplicate)
      when(jobRepository.existsByName("New Name")).thenReturn(false);
      when(machineTypeService.getTypeByUuid("new-m")).thenReturn(newType);
      when(jobRepository.update(any(Job.class)))
            .thenAnswer(
                  i -> {
                     Job j = i.getArgument(0);
                     // simulate persistence returning the same job
                     return j;
                  });

      JobDto updated = jobService.updateJob("job-1", dto);

      assertEquals("job-1", updated.getJobUuid());
      assertEquals("New Name", updated.getName());
      assertEquals("new-m", updated.getRequiredMachineTypeUuid());
   }

   @Test
   void updateJob_duplicateName_throwsException() {
      MachineType existingType = new MachineType();
      existingType.setMachineTypeUuid("m");
      Job existing = new Job(
            "Original",
            "desc",
            10,
            LocalDateTime.now().plusDays(1),
            existingType,
            null);
      existing.setJobUuid("job-2");

      JobDto dto = new JobDto(
            "job-2",
            null,
            "Another Name",
            "desc",
            10,
            LocalDateTime.now().plusDays(1),
            "m");

      when(jobRepository.findByUuid("job-2")).thenReturn(Optional.of(existing));
      // name changed and the new name already exists
      when(jobRepository.existsByName("Another Name")).thenReturn(true);

      assertThrows(DuplicateJobNameException.class, () -> jobService.updateJob("job-2", dto));
   }

   @Test
   void updateJob_whenMissing_throwsJobNotFound() {
      JobDto dto = new JobDto(
            "job-xxx",
            null,
            "Name",
            "desc",
            10,
            LocalDateTime.now().plusDays(1),
            "m");

      when(jobRepository.findByUuid("job-xxx")).thenReturn(Optional.empty());
      assertThrows(JobNotFoundException.class, () -> jobService.updateJob("job-xxx", dto));
   }

   // ----------------------------
   // Delete tests
   // ----------------------------

   @Test
   void deleteJob_whenExists_deletesSuccessfully() {
      when(jobRepository.existsByUuid("del-1")).thenReturn(true);
      when(scheduleRepository.findScheduleContainingJob("del-1"))
            .thenReturn(Optional.empty()); // ← ADD THIS
      doNothing().when(jobRepository).deleteByUuid("del-1");

      jobService.deleteJob("del-1");

      verify(jobRepository, times(1)).deleteByUuid("del-1");
   }

   @Test
   void deleteJob_whenMissing_throwsJobNotFound() {
      when(jobRepository.existsByUuid("nope")).thenReturn(false);
      assertThrows(JobNotFoundException.class, () -> jobService.deleteJob("nope"));
      verify(jobRepository, never()).deleteByUuid(any());
   }

   // ----------------------------
   // Find by machine type
   // ----------------------------

   @Test
   void findByRequiredMachineType_returnsMappedDtos() {
      MachineType m = new MachineType();
      m.setMachineTypeUuid("mt-1");

      Job j1 = new Job("X", "d", 5, LocalDateTime.now().plusDays(1), m, null);
      j1.setJobUuid("x1");
      Job j2 = new Job("Y", "d2", 15, LocalDateTime.now().plusDays(2), m, null);
      j2.setJobUuid("y2");

      when(jobRepository.findByRequiredMachineType(m)).thenReturn(List.of(j1, j2));

      List<JobDto> results = jobService.findByRequiredMachineType(m);

      assertEquals(2, results.size());
      assertEquals("x1", results.get(0).getJobUuid());
      assertEquals("mt-1", results.get(0).getRequiredMachineTypeUuid());
   }

   @Test
   void updateJob_withTemplateUuid_loadsTemplate_and_returnsDtoWithTemplate() {
      MachineType existingType = new MachineType();
      existingType.setMachineTypeUuid("old-m");
      Job existing = new Job("Old", "oldDesc", 10, LocalDateTime.now().plusDays(1), existingType, null);
      existing.setJobUuid("job-tpl-1");

      JobDto dto = new JobDto(
            "job-tpl-1",
            "tpl-123",
            "New",
            "desc",
            30,
            LocalDateTime.now().plusDays(2),
            "mt-1");

      MachineType required = new MachineType();
      required.setMachineTypeUuid("mt-1");

      JobTemplate template = new JobTemplate();
      template.setJobTemplateUuid("tpl-123");

      when(jobRepository.findByUuid("job-tpl-1")).thenReturn(Optional.of(existing));
      when(jobRepository.existsByName("New")).thenReturn(false);
      when(machineTypeService.getTypeByUuid("mt-1")).thenReturn(required);
      when(jobTemplateService.getJobTemplateByUuid("tpl-123")).thenReturn(template);
      when(jobRepository.update(any(Job.class))).thenAnswer(i -> i.getArgument(0));

      JobDto updated = jobService.updateJob("job-tpl-1", dto);

      assertEquals("job-tpl-1", updated.getJobUuid());
      assertEquals("tpl-123", updated.getTemplateUuid());
      verify(jobTemplateService).getJobTemplateByUuid("tpl-123");
   }

   @Test
   void findJobsInDeadlineRange_returnsMappedDtos() {
      MachineType mt = new MachineType();
      mt.setMachineTypeUuid("mt-1");

      Job j1 = new Job("A", "d", 10, LocalDateTime.now().plusDays(1), mt, null);
      j1.setJobUuid("j1");
      Job j2 = new Job("B", "d2", 20, LocalDateTime.now().plusDays(2), mt, null);
      j2.setJobUuid("j2");

      LocalDateTime start = LocalDateTime.now();
      LocalDateTime end = LocalDateTime.now().plusDays(3);

      when(jobRepository.findByDeadlineBetween(start, end)).thenReturn(List.of(j1, j2));

      List<JobDto> results = jobService.findJobsInDeadlineRange(start, end);

      assertEquals(2, results.size());
      assertEquals("j1", results.get(0).getJobUuid());
      assertEquals("mt-1", results.get(0).getRequiredMachineTypeUuid());
   }

   @Test
   void deleteJob_whenScheduleConflictFound_throwsScheduledJobConflictException() {
      when(jobRepository.existsByUuid("job-1")).thenReturn(true);

      Schedule schedule = new Schedule();
      schedule.setScheduleUuid("schedule-123");
      when(scheduleRepository.findScheduleContainingJob("job-1")).thenReturn(Optional.of(schedule));

      ScheduledJobConflictException ex = assertThrows(ScheduledJobConflictException.class,
            () -> jobService.deleteJob("job-1"));

      assertTrue(ex.getMessage().contains("job-1"));
      verify(jobRepository, never()).deleteByUuid(any());
   }

   @Test
   void deleteJob_whenScheduleLookupThrowsGenericException_continuesAndDeletes() {
      when(jobRepository.existsByUuid("job-2")).thenReturn(true);

      // Simulate unexpected error during conflict lookup: should be caught and
      // deletion continues.
      when(scheduleRepository.findScheduleContainingJob("job-2"))
            .thenThrow(new RuntimeException("DB temporarily unavailable"));

      doNothing().when(jobRepository).deleteByUuid("job-2");

      assertDoesNotThrow(() -> jobService.deleteJob("job-2"));
      verify(jobRepository).deleteByUuid("job-2");
   }

   @Test
   void deleteJob_whenDeleteThrowsNonFkError_wrapsInIllegalStateException() {
      when(jobRepository.existsByUuid("job-3")).thenReturn(true);
      when(scheduleRepository.findScheduleContainingJob("job-3")).thenReturn(Optional.empty());

      doThrow(new RuntimeException("some other failure"))
            .when(jobRepository).deleteByUuid("job-3");

      IllegalStateException ex = assertThrows(IllegalStateException.class, () -> jobService.deleteJob("job-3"));
      assertTrue(ex.getMessage().toLowerCase().contains("failed to delete job"));
   }

   @Test
   void deleteJob_whenDeleteThrowsFkError_andScheduleFound_throwsScheduledJobConflictException() {
      when(jobRepository.existsByUuid("job-4")).thenReturn(true);
      when(scheduleRepository.findScheduleContainingJob("job-4")).thenReturn(Optional.empty());

      // Trigger FK branch by message containing "foreign key"
      doThrow(new RuntimeException("FOREIGN KEY constraint fails"))
            .when(jobRepository).deleteByUuid("job-4");

      Schedule schedule = new Schedule();
      schedule.setScheduleUuid("schedule-fk-1");

      // On FK error, service tries again to locate the conflicting schedule
      when(scheduleRepository.findScheduleContainingJob("job-4")).thenReturn(Optional.of(schedule));

      assertThrows(ScheduledJobConflictException.class, () -> jobService.deleteJob("job-4"));
   }

   @Test
   void deleteJob_whenDeleteThrowsFkError_andScheduleLookupFails_wrapsIllegalStateException() {
      when(jobRepository.existsByUuid("job-5")).thenReturn(true);
      when(scheduleRepository.findScheduleContainingJob("job-5")).thenReturn(Optional.empty());

      // Trigger FK branch using the known constraint name substring too
      doThrow(new RuntimeException("fktg2i8rwld0yc9jn0s4kixiawk"))
            .when(jobRepository).deleteByUuid("job-5");

      // Fallback lookup throws another exception; should be caught and then wrapped
      // as IllegalStateException
      when(scheduleRepository.findScheduleContainingJob("job-5"))
            .thenThrow(new RuntimeException("cannot determine schedule"));

      IllegalStateException ex = assertThrows(IllegalStateException.class, () -> jobService.deleteJob("job-5"));
      assertTrue(ex.getMessage().toLowerCase().contains("failed to delete job"));
   }
}
