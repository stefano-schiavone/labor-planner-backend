package com.laborplanner.backend.machineStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.laborplanner.backend.exception.machine.DuplicateMachineStatusNameException;
import com.laborplanner.backend.exception.machine.MachineStatusNotFoundException;
import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.repository.MachineStatusRepository;
import com.laborplanner.backend.service.MachineStatusService;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MachineStatusServiceTest {

   @Mock
   private MachineStatusRepository machineStatusRepository;

   @InjectMocks
   private MachineStatusService machineStatusService;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
   }

   // -------------------------------------------------
   // Get all statuses
   // -------------------------------------------------

   @Test
   void getAllStatuses_returnsList() {
      MachineStatus s1 = new MachineStatus();
      s1.setMachineStatusUuid("s1");
      s1.setName("Online");

      MachineStatus s2 = new MachineStatus();
      s2.setMachineStatusUuid("s2");
      s2.setName("Offline");

      when(machineStatusRepository.findAllByOrderByNameAsc()).thenReturn(List.of(s1, s2));

      List<MachineStatus> results = machineStatusService.getAllStatuses();

      assertEquals(2, results.size());
      assertEquals("Online", results.get(0).getName());
   }

   // -------------------------------------------------
   // Get by UUID
   // -------------------------------------------------

   @Test
   void getStatusByUuid_whenExists_returnsStatus() {
      MachineStatus s = new MachineStatus();
      s.setMachineStatusUuid("abc");
      s.setName("Operational");

      when(machineStatusRepository.findByUuid("abc")).thenReturn(Optional.of(s));

      MachineStatus result = machineStatusService.getStatusByUuid("abc");

      assertEquals("abc", result.getMachineStatusUuid());
      assertEquals("Operational", result.getName());
   }

   @Test
   void getStatusByUuid_whenMissing_throwsException() {
      when(machineStatusRepository.findByUuid("missing")).thenReturn(Optional.empty());
      assertThrows(MachineStatusNotFoundException.class,
            () -> machineStatusService.getStatusByUuid("missing"));
   }

   // -------------------------------------------------
   // Create status
   // -------------------------------------------------

   @Test
   void createStatus_withDuplicateName_throwsException() {
      MachineStatus s = new MachineStatus();
      s.setName("Running");

      when(machineStatusRepository.existsByName("Running")).thenReturn(true);

      assertThrows(DuplicateMachineStatusNameException.class,
            () -> machineStatusService.createStatus(s));

      verify(machineStatusRepository, times(1)).existsByName("Running");
      verify(machineStatusRepository, never()).create(any());
   }

   @Test
   void createStatus_withValidName_createsSuccessfully() {
      MachineStatus input = new MachineStatus();
      input.setName("Idle");

      MachineStatus created = new MachineStatus();
      created.setMachineStatusUuid("uuid-123");
      created.setName("Idle");

      when(machineStatusRepository.existsByName("Idle")).thenReturn(false);
      when(machineStatusRepository.create(any(MachineStatus.class))).thenReturn(created);

      MachineStatus result = machineStatusService.createStatus(input);

      assertNotNull(result);
      assertEquals("uuid-123", result.getMachineStatusUuid());
      assertEquals("Idle", result.getName());
   }

   // -------------------------------------------------
   // Update status
   // -------------------------------------------------

   @Test
   void updateStatus_whenExists_updatesSuccessfully() {
      MachineStatus existing = new MachineStatus();
      existing.setMachineStatusUuid("st1");
      existing.setName("Old Name");

      MachineStatus update = new MachineStatus();
      update.setName("New Name");

      when(machineStatusRepository.findByUuid("st1")).thenReturn(Optional.of(existing));
      when(machineStatusRepository.update(any(MachineStatus.class)))
            .thenAnswer(inv -> inv.getArgument(0));

      MachineStatus result = machineStatusService.updateStatus("st1", update);

      assertEquals("New Name", result.getName());
   }

   @Test
   void updateStatus_whenNotFound_throwsException() {
      MachineStatus update = new MachineStatus();
      update.setName("X");

      when(machineStatusRepository.findByUuid("nope")).thenReturn(Optional.empty());

      assertThrows(MachineStatusNotFoundException.class,
            () -> machineStatusService.updateStatus("nope", update));
   }

   // -------------------------------------------------
   // Delete status
   // -------------------------------------------------

   @Test
   void deleteStatus_whenExists_deletesSuccessfully() {
      when(machineStatusRepository.existsByUuid("del-1")).thenReturn(true);
      doNothing().when(machineStatusRepository).deleteByUuid("del-1");

      machineStatusService.deleteStatus("del-1");

      verify(machineStatusRepository, times(1)).deleteByUuid("del-1");
   }

   @Test
   void deleteStatus_whenNotFound_throwsException() {
      when(machineStatusRepository.existsByUuid("missing")).thenReturn(false);

      assertThrows(MachineStatusNotFoundException.class,
            () -> machineStatusService.deleteStatus("missing"));

      verify(machineStatusRepository, never()).deleteByUuid(any());
   }

   // -------------------------------------------------
   // Find by name
   // -------------------------------------------------

   @Test
   void findByName_returnsOptional() {
      MachineStatus s = new MachineStatus();
      s.setName("Working");

      when(machineStatusRepository.findByName("Working")).thenReturn(Optional.of(s));

      Optional<MachineStatus> result = machineStatusService.findByName("Working");

      assertTrue(result.isPresent());
      assertEquals("Working", result.get().getName());
   }
}
