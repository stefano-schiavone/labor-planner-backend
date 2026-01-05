package com.laborplanner.backend.schedule;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.laborplanner.backend.dto.schedule.ScheduleDto;
import com.laborplanner.backend.exception.schedule.ScheduleInfeasibleException;
import com.laborplanner.backend.model.*;
import com.laborplanner.backend.repository.JobRepository;
import com.laborplanner.backend.repository.MachineRepository;
import com.laborplanner.backend.repository.MachineTypeRepository;
import com.laborplanner.backend.repository.ScheduleRepository;
import com.laborplanner.backend.repository.UserRepository;
import com.laborplanner.backend.service.ScheduleService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

class ScheduleServiceTest {

   @Mock
   private SolverManager<Schedule, UUID> solverManager;

   @Mock
   private ScheduleRepository scheduleRepository;

   @Mock
   private JobRepository jobRepository;

   @Mock
   private MachineRepository machineRepository;

   @Mock
   private MachineTypeRepository machineTypeRepository;

   @Mock
   private UserRepository userRepository;

   @InjectMocks
   private ScheduleService scheduleService;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
      SecurityContextHolder.clearContext();
   }

   @AfterEach
   void tearDown() {
      SecurityContextHolder.clearContext();
   }

   // ----------------------------
   // getScheduleForWeek
   // ----------------------------

   @Test
   void getScheduleForWeek_whenMissing_returnsNull() {
      LocalDateTime ws = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime we = ws.plusDays(7);

      when(scheduleRepository.findByWeekStartDate(ws))
            .thenReturn(Optional.empty());

      ScheduleDto dto = scheduleService.getScheduleForWeek(ws, we);

      assertNull(dto);
   }

   @Test
   void getScheduleForWeek_whenFound_returnsDto() {
      LocalDateTime ws = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime we = ws.plusDays(7);

      Schedule schedule = new Schedule();
      schedule.setScheduleUuid("s1");
      schedule.setWeekStartDate(ws);
      schedule.setLastModifiedDate(ws.plusHours(1));
      schedule.setScheduledJobList(List.of());

      when(scheduleRepository.findByWeekStartDate(ws))
            .thenReturn(Optional.of(schedule));

      when(machineRepository.findAll()).thenReturn(List.of()); // used by toDto

      ScheduleDto dto = scheduleService.getScheduleForWeek(ws, we);

      assertNotNull(dto);
      assertEquals("s1", dto.getScheduleUuid());
      assertEquals(ws, dto.getWeekStartDate());
   }

   // ----------------------------
   // deleteSchedule
   // ----------------------------

   @Test
   void deleteSchedule_whenMissing_throws() {
      when(scheduleRepository.existsByUuid("missing")).thenReturn(false);

      assertThrows(IllegalArgumentException.class, () -> scheduleService.deleteSchedule("missing"));
      verify(scheduleRepository, never()).deleteByUuid(anyString());
   }

   @Test
   void deleteSchedule_whenExists_deletes() {
      when(scheduleRepository.existsByUuid("s1")).thenReturn(true);

      scheduleService.deleteSchedule("s1");

      verify(scheduleRepository).deleteByUuid("s1");
   }

   // ----------------------------
   // solveForWeek - early exits and auth failures
   // ----------------------------

   @Test
   void solveForWeek_whenNoJobs_throws() {
      LocalDateTime ws = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime we = ws.plusDays(7);

      when(jobRepository.findByDeadlineBetween(ws, we)).thenReturn(List.of());

      assertThrows(IllegalStateException.class, () -> scheduleService.solveForWeek(ws, we));
   }

   @Test
   void solveForWeek_whenNoAuthentication_throws() {
      LocalDateTime ws = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime we = ws.plusDays(7);

      Job job = new Job();
      job.setJobUuid("j1");
      job.setRequiredMachineTypeUuid("mt1");

      when(jobRepository.findByDeadlineBetween(ws, we)).thenReturn(List.of(job));

      // no auth in context
      assertThrows(IllegalStateException.class, () -> scheduleService.solveForWeek(ws, we));
   }

   @Test
   void solveForWeek_whenInvalidPrincipalType_throws() {
      LocalDateTime ws = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime we = ws.plusDays(7);

      Job job = new Job();
      job.setJobUuid("j1");
      job.setRequiredMachineTypeUuid("mt1");

      when(jobRepository.findByDeadlineBetween(ws, we)).thenReturn(List.of(job));

      // principal is String, not UserDetails
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("not-user-details", "pw",
            List.of());
      SecurityContextHolder.getContext().setAuthentication(auth);

      assertThrows(IllegalStateException.class, () -> scheduleService.solveForWeek(ws, we));
   }

   @Test
   void solveForWeek_whenUserNotFound_throws() {
      LocalDateTime ws = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime we = ws.plusDays(7);

      Job job = new Job();
      job.setJobUuid("j1");
      job.setRequiredMachineTypeUuid("mt1");

      when(jobRepository.findByDeadlineBetween(ws, we)).thenReturn(List.of(job));

      UserDetails userDetails = mock(UserDetails.class);
      when(userDetails.getUsername()).thenReturn("user@mail.com");
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, "pw", List.of());
      SecurityContextHolder.getContext().setAuthentication(auth);

      when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.empty());

      assertThrows(IllegalStateException.class, () -> scheduleService.solveForWeek(ws, we));
   }

   // ----------------------------
   // solveForWeek - delete existing schedule path
   // ----------------------------

   @Test
   void solveForWeek_whenExistingSchedule_present_deletesOldOne() throws Exception {
      LocalDateTime ws = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime we = ws.plusDays(7);

      Job job = new Job();
      job.setJobUuid("j1");
      job.setRequiredMachineTypeUuid("mt1");
      when(jobRepository.findByDeadlineBetween(ws, we)).thenReturn(List.of(job));

      // auth ok
      User user = new User();
      user.setName("User");
      user.setEmail("user@email.com");
      user.setPasswordHash("pw");
      mockAuthenticatedUser("user@mail.com", user);

      Schedule existing = new Schedule();
      existing.setScheduleUuid("old-schedule");
      when(scheduleRepository.findByWeekStartDate(ws)).thenReturn(Optional.of(existing));

      // setup machine types and machines so createScheduledJobs works
      MachineType mt = new MachineType();
      mt.setMachineTypeUuid("mt1");
      when(machineTypeRepository.findAll()).thenReturn(List.of(mt));

      Machine m = new Machine();
      m.setMachineUuid("m1");
      m.setType(mt);
      when(machineRepository.findAll()).thenReturn(List.of(m));

      // solver returns feasible (to pass through)
      Schedule solved = new Schedule();
      solved.setWeekStartDate(ws);
      solved.setScheduledJobList(List.of(new ScheduledJob()));
      solved.setScore(feasibleScore());

      SolverJob<Schedule, UUID> solverJob = mockSolverJobReturning(solved);
      when(solverManager.solve(any(UUID.class), any(Schedule.class))).thenReturn(solverJob);

      when(scheduleRepository.create(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

      ScheduleDto dto = scheduleService.solveForWeek(ws, we);

      assertNotNull(dto);
      verify(scheduleRepository).deleteByUuid("old-schedule");
   }

   // ----------------------------
   // solveForWeek - infeasible solution
   // ----------------------------

   @Test
   void solveForWeek_whenInfeasible_throwsScheduleInfeasibleException() throws Exception {
      LocalDateTime ws = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime we = ws.plusDays(7);

      Job job = new Job();
      job.setJobUuid("j1");
      job.setRequiredMachineTypeUuid("mt1");
      when(jobRepository.findByDeadlineBetween(ws, we)).thenReturn(List.of(job));

      User user = new User();
      user.setName("User");
      user.setEmail("user@email.com");
      user.setPasswordHash("pw");
      mockAuthenticatedUser("user@mail.com", user);

      when(scheduleRepository.findByWeekStartDate(ws)).thenReturn(Optional.empty());

      MachineType mt = new MachineType();
      mt.setMachineTypeUuid("mt1");
      when(machineTypeRepository.findAll()).thenReturn(List.of(mt));

      Machine m = new Machine();
      m.setMachineUuid("m1");
      m.setType(mt);
      when(machineRepository.findAll()).thenReturn(List.of(m));

      Schedule solved = new Schedule();
      solved.setScheduledJobList(List.of(new ScheduledJob()));
      solved.setScore(infeasibleScore());

      SolverJob<Schedule, UUID> solverJob = mockSolverJobReturning(solved);
      when(solverManager.solve(any(UUID.class), any(Schedule.class))).thenReturn(solverJob);

      assertThrows(ScheduleInfeasibleException.class, () -> scheduleService.solveForWeek(ws, we));
      verify(scheduleRepository, never()).create(any());
   }

   // ----------------------------
   // solveForWeek - happy path (persist + dto mapping)
   // ----------------------------

   @Test
   void solveForWeek_whenFeasible_persistsAndReturnsDto() throws Exception {
      LocalDateTime ws = LocalDateTime.of(2026, 1, 5, 0, 0);
      LocalDateTime we = ws.plusDays(7);

      Job job = new Job();
      job.setJobUuid("j1");
      job.setRequiredMachineTypeUuid("mt1");
      when(jobRepository.findByDeadlineBetween(ws, we)).thenReturn(List.of(job));

      User user = new User();
      user.setName("User");
      user.setEmail("user@email.com");
      user.setPasswordHash("pw");
      mockAuthenticatedUser("user@mail.com", user);

      when(scheduleRepository.findByWeekStartDate(ws)).thenReturn(Optional.empty());

      MachineType mt = new MachineType();
      mt.setMachineTypeUuid("mt1");
      when(machineTypeRepository.findAll()).thenReturn(List.of(mt));

      Machine m = new Machine();
      m.setMachineUuid("m1");
      m.setType(mt);
      when(machineRepository.findAll()).thenReturn(List.of(m));

      ScheduledJob sj = new ScheduledJob();
      sj.setScheduledJobUuid("sj-1");

      Schedule solved = new Schedule();
      solved.setScheduleUuid("solved-uuid");
      solved.setWeekStartDate(ws);
      solved.setScheduledJobList(List.of(sj));
      solved.setScore(feasibleScore());

      SolverJob<Schedule, UUID> solverJob = mockSolverJobReturning(solved);
      when(solverManager.solve(any(UUID.class), any(Schedule.class))).thenReturn(solverJob);

      // repository.create returns the created schedule; toDto will call
      // machineRepository.findAll() again
      when(scheduleRepository.create(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

      ScheduleDto dto = scheduleService.solveForWeek(ws, we);

      assertNotNull(dto);
      assertEquals(ws, dto.getWeekStartDate());
      assertNotNull(dto.getScheduledJobList());
      assertEquals(1, dto.getScheduledJobList().size());
   }

   // ----------------------------
   // solveSchedule (other public method)
   // ----------------------------

   @Test
   void solveSchedule_setsUuidAndPersists() throws Exception {
      LocalDateTime ws = LocalDateTime.of(2026, 1, 5, 0, 0);

      Job job = new Job();
      job.setJobUuid("j1");
      job.setRequiredMachineTypeUuid("mt1");

      Schedule problem = new Schedule();
      problem.setWeekStartDate(ws);
      problem.setJobList(List.of(job));

      MachineType mt = new MachineType();
      mt.setMachineTypeUuid("mt1");
      when(machineTypeRepository.findAll()).thenReturn(List.of(mt));

      Machine m = new Machine();
      m.setMachineUuid("m1");
      m.setType(mt);
      when(machineRepository.findAll()).thenReturn(List.of(m));

      Schedule solved = new Schedule();
      solved.setWeekStartDate(ws);
      solved.setScheduledJobList(List.of(new ScheduledJob()));
      // scheduleUuid is null intentionally: ScheduleService should set it
      solved.setScheduleUuid(null);

      SolverJob<Schedule, UUID> solverJob = mockSolverJobReturning(solved);
      when(solverManager.solve(any(UUID.class), any(Schedule.class))).thenReturn(solverJob);

      when(scheduleRepository.create(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

      Schedule created = scheduleService.solveSchedule(problem);

      assertNotNull(created);
      assertNotNull(created.getScheduleUuid(), "scheduleUuid should be generated");
      assertNotNull(created.getLastModifiedDate(), "lastModifiedDate should be set");
      verify(scheduleRepository).create(any(Schedule.class));
   }

   // ----------------------------
   // createScheduledJobs (private) via reflection to cover branches
   // ----------------------------

   @Test
   void createScheduledJobs_whenJobMissingRequiredMachineTypeUuid_throws() throws Exception {
      Job job = new Job();
      job.setJobUuid("j1");
      job.setRequiredMachineTypeUuid(null);

      when(machineTypeRepository.findAll()).thenReturn(List.of());

      Method m = ScheduleService.class.getDeclaredMethod("createScheduledJobs", List.class, List.class);
      m.setAccessible(true);

      InvocationTargetException ex = assertThrows(InvocationTargetException.class,
            () -> m.invoke(scheduleService, List.of(job), List.of()));
      assertTrue(ex.getCause() instanceof IllegalStateException);
   }

   @Test
   void createScheduledJobs_whenMachineTypeNotFound_throws() throws Exception {
      Job job = new Job();
      job.setJobUuid("j1");
      job.setRequiredMachineTypeUuid("missing-mt");

      when(machineTypeRepository.findAll()).thenReturn(List.of()); // cache empty

      Method m = ScheduleService.class.getDeclaredMethod("createScheduledJobs", List.class, List.class);
      m.setAccessible(true);

      InvocationTargetException ex = assertThrows(InvocationTargetException.class,
            () -> m.invoke(scheduleService, List.of(job), List.of()));
      assertTrue(ex.getCause() instanceof IllegalStateException);
   }

   @Test
   void createScheduledJobs_populatesCompatibleMachinesAndUuid() throws Exception {
      MachineType mt1 = new MachineType();
      mt1.setMachineTypeUuid("mt1");

      when(machineTypeRepository.findAll()).thenReturn(List.of(mt1));

      Job job = new Job();
      job.setJobUuid("j1");
      job.setRequiredMachineTypeUuid("mt1");

      Machine compatible = new Machine();
      compatible.setMachineUuid("m1");
      compatible.setType(mt1);

      Machine incompatible = new Machine();
      incompatible.setMachineUuid("m2");
      MachineType mt2 = new MachineType();
      mt2.setMachineTypeUuid("mt2");
      incompatible.setType(mt2);

      Method method = ScheduleService.class.getDeclaredMethod("createScheduledJobs", List.class, List.class);
      method.setAccessible(true);

      @SuppressWarnings("unchecked")
      List<ScheduledJob> result = (List<ScheduledJob>) method.invoke(scheduleService, List.of(job),
            List.of(compatible, incompatible));

      assertEquals(1, result.size());
      ScheduledJob sj = result.get(0);

      assertNotNull(sj.getScheduledJobUuid(), "ScheduledJob UUID should be set for PlanningId");
      assertNotNull(sj.getCompatibleMachines());
      assertEquals(1, sj.getCompatibleMachines().size());
      assertEquals("m1", sj.getCompatibleMachines().get(0).getMachineUuid());
      assertNotNull(job.getRequiredMachineType(), "Job.requiredMachineType should be populated");
      assertEquals("mt1", job.getRequiredMachineType().getMachineTypeUuid());
   }

   // ----------------------------
   // Helpers
   // ----------------------------

   private void mockAuthenticatedUser(String email, User userEntity) {
      UserDetails userDetails = mock(UserDetails.class);
      when(userDetails.getUsername()).thenReturn(email);
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, "pw", List.of());
      SecurityContextHolder.getContext().setAuthentication(auth);

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
   }

   private SolverJob<Schedule, UUID> mockSolverJobReturning(Schedule solved) throws Exception {
      @SuppressWarnings("unchecked")
      SolverJob<Schedule, UUID> solverJob = mock(SolverJob.class);
      // getFinalBestSolution throws InterruptedException/ExecutionException in
      // signature
      when(solverJob.getFinalBestSolution()).thenReturn(solved);
      return solverJob;
   }

   private HardSoftScore feasibleScore() {
      return HardSoftScore.of(0, 0); // feasible
   }

   private HardSoftScore infeasibleScore() {
      return HardSoftScore.of(-1, 0); // hard < 0 => infeasible
   }
}
