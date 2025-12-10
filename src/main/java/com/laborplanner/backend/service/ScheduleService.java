package com.laborplanner.backend.service;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laborplanner.backend.model.*;
import com.laborplanner.backend.repository.MachineRepository;
import com.laborplanner.backend.repository.MachineTypeRepository;

import jakarta.transaction.Transactional;

@Service
public class ScheduleService {

   @Autowired
   private SolverManager<Schedule, UUID> solverManager;

   @Autowired
   private MachineRepository machineRepository;

   @Autowired
   private MachineTypeRepository machineTypeRepository;

   @Transactional
   public Schedule solveSchedule(Schedule problem) {
      UUID problemId = UUID.randomUUID();

      // Generate time grains
      problem.setTimeGrainList(Schedule.generateTimeGrains());

      // Load machines from DB to ensure type objects are populated
      List<Machine> allMachines = machineRepository.findAll();

      // Prepare ScheduledJob entities
      List<ScheduledJob> scheduledJobs = createScheduledJobs(problem.getJobList(), allMachines);
      problem.setScheduledJobList(scheduledJobs);

      // Solve with OptaPlanner
      SolverJob<Schedule, UUID> solverJob = solverManager.solve(problemId, problem);
      try {
         return solverJob.getFinalBestSolution();
      } catch (InterruptedException | ExecutionException e) {
         throw new IllegalStateException("Solving failed.", e);
      }
   }

   private List<ScheduledJob> createScheduledJobs(List<Job> jobs, List<Machine> machines) {
      List<ScheduledJob> scheduledJobs = new ArrayList<>();

      for (Job job : jobs) {
         if (job.getRequiredMachineTypeUuid() == null) {
            throw new IllegalStateException("Job " + job.getJobUuid() + " has no requiredMachineTypeUuid");
         }

         // Load the required MachineType object from DB using UUID
         MachineType requiredType = machineTypeRepository.findByUuid(job.getRequiredMachineTypeUuid())
               .orElseThrow(() -> new IllegalStateException(
                     "MachineType not found: " + job.getRequiredMachineTypeUuid()));

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

}
