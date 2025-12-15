package com.laborplanner.backend.service;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.stereotype.Service;

import com.laborplanner.backend.dto.schedule.ScheduleDto;
import com.laborplanner.backend.dto.scheduledJob.ScheduledJobDto;
import com.laborplanner.backend.exception.schedule.ScheduleInfeasibleException;
import com.laborplanner.backend.model.*;
import com.laborplanner.backend.repository.JobRepository;
import com.laborplanner.backend.repository.MachineRepository;
import com.laborplanner.backend.repository.MachineTypeRepository;
import com.laborplanner.backend.repository.ScheduleRepository;
import com.laborplanner.backend.repository.UserRepository;
import com.laborplanner.backend.service.interfaces.IScheduleService;

import jakarta.transaction.Transactional;

@Service
public class ScheduleService implements IScheduleService {

   private final SolverManager<Schedule, UUID> solverManager;
   private final ScheduleRepository scheduleRepository;
   private final JobRepository jobRepository;
   private final MachineRepository machineRepository;
   private final MachineTypeRepository machineTypeRepository;
   private final UserRepository userRepository;

   public ScheduleService(
         SolverManager<Schedule, UUID> solverManager,
         ScheduleRepository scheduleRepository,
         JobRepository jobRepository,
         MachineRepository machineRepository,
         MachineTypeRepository machineTypeRepository,
         UserRepository userRepository) {
      this.solverManager = solverManager;
      this.scheduleRepository = scheduleRepository;
      this.jobRepository = jobRepository;
      this.machineRepository = machineRepository;
      this.machineTypeRepository = machineTypeRepository;
      this.userRepository = userRepository;
   }

   @Override
   @Transactional
   public ScheduleDto getScheduleForWeek(LocalDateTime weekStart, LocalDateTime weekEnd) {
      Schedule schedule = scheduleRepository.findByWeekStartDate(weekStart)
            .stream().findFirst().orElse(null);
      return schedule != null ? toDto(schedule) : null;
   }

   @Override
   @Transactional
   public Schedule solveSchedule(Schedule problem) {
      UUID problemId = UUID.randomUUID();

      // Generate time grains
      problem.setTimeGrainList(Schedule.generateTimeGrains(problem.getWeekStartDate().toLocalDate()));

      // Load machines from DB to ensure type objects are populated
      List<Machine> allMachines = machineRepository.findAll();

      // Prepare ScheduledJob entities
      List<ScheduledJob> scheduledJobs = createScheduledJobs(problem.getJobList(), allMachines);
      problem.setScheduledJobList(scheduledJobs);

      // Solve with OptaPlanner
      SolverJob<Schedule, UUID> solverJob = solverManager.solve(problemId, problem);
      try {
         Schedule solved = solverJob.getFinalBestSolution();

         // Ensure UUID
         if (solved.getScheduleUuid() == null) {
            solved.setScheduleUuid(UUID.randomUUID().toString());
         }

         // Set last modified
         solved.setLastModifiedDate(LocalDateTime.now());

         // Persist
         return scheduleRepository.create(solved);
      } catch (InterruptedException | ExecutionException e) {
         throw new IllegalStateException("Solving failed.", e);
      }
   }

   private List<ScheduledJob> createScheduledJobs(List<Job> jobs, List<Machine> machines) {
      List<ScheduledJob> scheduledJobs = new ArrayList<>();

      Map<String, MachineType> machineTypeCache = machineTypeRepository.findAll().stream()
            .collect(Collectors.toMap(MachineType::getMachineTypeUuid, mt -> mt));

      for (Job job : jobs) {
         if (job.getRequiredMachineTypeUuid() == null) {
            throw new IllegalStateException("Job " + job.getJobUuid() + " has no requiredMachineTypeUuid");
         }

         // Load the required MachineType object from DB using UUID
         MachineType requiredType = machineTypeCache.get(job.getRequiredMachineTypeUuid());
         if (requiredType == null)
            throw new IllegalStateException("MachineType not found: " + job.getRequiredMachineTypeUuid());

         // Populate the full object for internal logic
         job.setRequiredMachineType(requiredType);

         // Filter compatible machines
         List<Machine> compatibleMachines = machines.stream()
               .filter(m -> m.getType() != null)
               .filter(m -> m.getType().getMachineTypeUuid().equals(requiredType.getMachineTypeUuid()))
               .collect(Collectors.toList());

         ScheduledJob sj = new ScheduledJob(job, null, null);
         sj.setCompatibleMachines(compatibleMachines);
         // I need to populate uuid field because OptaPlanner @PlanningId needs it
         // populated
         sj.setScheduledJobUuid(UUID.randomUUID().toString());

         scheduledJobs.add(sj);
      }

      return scheduledJobs;
   }

   @Override
   @Transactional
   public Schedule solveForWeek(LocalDateTime weekStart, LocalDateTime weekEnd) {

      // 1. Load jobs with deadlines in this week
      List<Job> jobs = jobRepository
            .findByDeadlineBetween(weekStart, weekEnd);

      if (jobs.isEmpty()) {
         throw new IllegalStateException("No jobs found for selected week.");
      }

      // 2. Create schedule
      Schedule schedule = new Schedule();
      schedule.setScheduleUuid(UUID.randomUUID().toString());
      schedule.setWeekStartDate(weekStart);
      schedule.setJobList(jobs);
      User user = userRepository.findByUuid("0d5965dc-5fda-4441-98e8-8fcf6b1ccfb6")
            .orElseThrow(() -> new IllegalStateException("User not found"));
      schedule.setCreatedByUser(user);

      // 3. Generate time grains
      schedule.setTimeGrainList(Schedule.generateTimeGrains(weekStart.toLocalDate()));

      // 4. Load machines
      List<Machine> machines = machineRepository.findAll();
      schedule.setMachineList(machines);

      // 5. Create ScheduledJobs
      List<ScheduledJob> scheduledJobs = createScheduledJobs(jobs, machines);

      schedule.setScheduledJobList(scheduledJobs);

      // 6. Solve
      SolverJob<Schedule, UUID> solverJob = solverManager.solve(UUID.randomUUID(), schedule);

      try {
         Schedule solved = solverJob.getFinalBestSolution();
         solved.setLastModifiedDate(LocalDateTime.now());

         if (solved.getScore() == null || !solved.getScore().isFeasible()) {
            throw new ScheduleInfeasibleException();
         }

         // Null out UUIDs so Hibernate treats as new rows
         solved.setScheduleUuid(null);
         solved.getScheduledJobList().forEach(job -> job.setScheduledJobUuid(null));

         return scheduleRepository.create(solved);
      } catch (InterruptedException | ExecutionException e) {
         throw new IllegalStateException("Solving failed", e);
      }
   }

   // Converting methods

   private ScheduleDto toDto(Schedule schedule) {
      List<ScheduledJobDto> scheduledJobDtos = schedule.getScheduledJobList()
            .stream()
            .map(this::toScheduledJobDto)
            .toList();

      // NOTE: Keeping Machines here for simplicity (not DTO)
      List<Machine> machineList = machineRepository.findAll();

      return new ScheduleDto(
            schedule.getScheduleUuid(),
            schedule.getWeekStartDate(),
            schedule.getLastModifiedDate(),
            scheduledJobDtos,
            machineList);
   }

   private ScheduledJobDto toScheduledJobDto(ScheduledJob scheduledJob) {
      return new ScheduledJobDto(
            scheduledJob.getScheduledJobUuid(),
            scheduledJob.getJob(),
            scheduledJob.getMachine(),
            scheduledJob.getStartingTimeGrain());
   }
}
