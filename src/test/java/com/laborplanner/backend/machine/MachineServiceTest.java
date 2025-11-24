package com.laborplanner.backend.machine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import com.laborplanner.backend.dto.machine.MachineDto;
import com.laborplanner.backend.exception.machine.DuplicateMachineNameException;
import com.laborplanner.backend.exception.machine.MachineNotFoundException;
import com.laborplanner.backend.exception.machine.NoMachineTypesExistException;
import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.MachineRepository;
import com.laborplanner.backend.service.MachineService;
import com.laborplanner.backend.service.interfaces.IMachineStatusReadService;
import com.laborplanner.backend.service.interfaces.IMachineTypeReadService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MachineServiceTest {

   @Mock
   private MachineRepository machineRepository;
   @Mock
   private IMachineTypeReadService machineTypeService;
   @Mock
   private IMachineStatusReadService machineStatusService;

   @InjectMocks
   private MachineService machineService;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
   }

   // ----------------------------
   // Get machines
   // ----------------------------

   @Test
   void getAllMachines_returnsMappedDtos() {
      MachineType t = new MachineType();
      t.setMachineTypeUuid("t1");
      MachineStatus s = new MachineStatus();
      s.setMachineStatusUuid("s1");

      Machine m1 = new Machine("M1", "desc1", t, s);
      m1.setMachineUuid("u1");

      Machine m2 = new Machine("M2", "desc2", t, s);
      m2.setMachineUuid("u2");

      when(machineRepository.findAll()).thenReturn(List.of(m1, m2));

      List<MachineDto> dtos = machineService.getAllMachines();

      assertEquals(2, dtos.size());
      assertEquals("u1", dtos.get(0).getMachineUuid());
      assertEquals("t1", dtos.get(0).getMachineTypeUuid());
   }

   @Test
   void getMachineByUuid_whenExists_returnsDto() {
      MachineType t = new MachineType();
      t.setMachineTypeUuid("t1");
      MachineStatus s = new MachineStatus();
      s.setMachineStatusUuid("s1");

      Machine m = new Machine("Machine", "desc", t, s);
      m.setMachineUuid("m123");

      when(machineRepository.findByUuid("m123")).thenReturn(Optional.of(m));

      MachineDto dto = machineService.getMachineByUuid("m123");

      assertEquals("m123", dto.getMachineUuid());
      assertEquals("Machine", dto.getName());
   }

   @Test
   void getMachineByUuid_whenMissing_throwsException() {
      when(machineRepository.findByUuid("missing")).thenReturn(Optional.empty());
      assertThrows(MachineNotFoundException.class, () -> machineService.getMachineByUuid("missing"));
   }

   // ----------------------------
   // Create machine
   // ----------------------------

   @Test
   void createMachine_whenNoMachineTypesExist_throwsException() {
      MachineDto dto = new MachineDto(null, "Name", "desc", "t1", "s1");

      when(machineTypeService.getAllTypes()).thenReturn(List.of());

      assertThrows(NoMachineTypesExistException.class, () -> machineService.createMachine(dto));
   }

   @Test
   void createMachine_whenDuplicateName_throwsException() {
      MachineDto dto = new MachineDto(null, "Name", "desc", "t1", "s1");

      MachineType type = new MachineType();
      type.setMachineTypeUuid("t1");
      MachineStatus status = new MachineStatus();
      status.setMachineStatusUuid("s1");

      when(machineTypeService.getAllTypes()).thenReturn(List.of(type));
      when(machineTypeService.getTypeByUuid("t1")).thenReturn(type);
      when(machineStatusService.getStatusByUuid("s1")).thenReturn(status);
      when(machineRepository.existsByName("Name")).thenReturn(true);

      assertThrows(DuplicateMachineNameException.class, () -> machineService.createMachine(dto));
      verify(machineRepository, never()).create(any());
   }

   @Test
   void createMachine_withValidData_createsMachineSuccessfully() {
      MachineDto dto = new MachineDto(null, "Machine-1", "desc", "t1", "s1");

      MachineType type = new MachineType();
      type.setMachineTypeUuid("t1");
      MachineStatus status = new MachineStatus();
      status.setMachineStatusUuid("s1");

      Machine model = new Machine("Machine-1", "desc", type, status);
      model.setMachineUuid("generated-123");

      when(machineTypeService.getAllTypes()).thenReturn(List.of(type));
      when(machineTypeService.getTypeByUuid("t1")).thenReturn(type);
      when(machineStatusService.getStatusByUuid("s1")).thenReturn(status);
      when(machineRepository.existsByName("Machine-1")).thenReturn(false);
      when(machineRepository.create(any(Machine.class)))
            .thenAnswer(
                  i -> {
                     Machine m = i.getArgument(0);
                     m.setMachineUuid("generated-123");
                     return m;
                  });

      MachineDto created = machineService.createMachine(dto);

      assertEquals("generated-123", created.getMachineUuid());
      assertEquals("Machine-1", created.getName());
   }

   // ----------------------------
   // Update machine
   // ----------------------------

   @Test
   void updateMachine_whenMissing_throwsException() {
      MachineDto dto = new MachineDto(null, "New", "desc", "t1", "s1");
      when(machineRepository.findByUuid("missing")).thenReturn(Optional.empty());
      assertThrows(MachineNotFoundException.class, () -> machineService.updateMachine("missing", dto));
   }

   @Test
   void updateMachine_duplicateName_throwsException() {
      MachineType t = new MachineType();
      t.setMachineTypeUuid("oldT");
      MachineStatus s = new MachineStatus();
      s.setMachineStatusUuid("oldS");

      Machine existing = new Machine("OldName", "oldDesc", t, s);
      existing.setMachineUuid("id-1");

      MachineDto dto = new MachineDto("id-1", "NewName", "desc", "t2", "s2");

      MachineType newT = new MachineType();
      newT.setMachineTypeUuid("t2");
      MachineStatus newS = new MachineStatus();
      newS.setMachineStatusUuid("s2");

      when(machineRepository.findByUuid("id-1")).thenReturn(Optional.of(existing));
      when(machineTypeService.getTypeByUuid("t2")).thenReturn(newT);
      when(machineStatusService.getStatusByUuid("s2")).thenReturn(newS);
      when(machineRepository.existsByName("NewName")).thenReturn(true);

      assertThrows(DuplicateMachineNameException.class, () -> machineService.updateMachine("id-1", dto));
   }

   @Test
   void updateMachine_successful_update_returnsUpdatedDto() {
      MachineType oldT = new MachineType();
      oldT.setMachineTypeUuid("tOld");
      MachineStatus oldS = new MachineStatus();
      oldS.setMachineStatusUuid("sOld");

      Machine existing = new Machine("Old", "OldDesc", oldT, oldS);
      existing.setMachineUuid("m1");

      MachineType newT = new MachineType();
      newT.setMachineTypeUuid("tNew");
      MachineStatus newS = new MachineStatus();
      newS.setMachineStatusUuid("sNew");

      MachineDto dto = new MachineDto("m1", "NewName", "NewDesc", "tNew", "sNew");

      when(machineRepository.findByUuid("m1")).thenReturn(Optional.of(existing));
      when(machineTypeService.getTypeByUuid("tNew")).thenReturn(newT);
      when(machineStatusService.getStatusByUuid("sNew")).thenReturn(newS);
      when(machineRepository.existsByName("NewName")).thenReturn(false);
      when(machineRepository.update(any(Machine.class)))
            .thenAnswer(i -> i.getArgument(0));

      MachineDto updated = machineService.updateMachine("m1", dto);

      assertEquals("m1", updated.getMachineUuid());
      assertEquals("NewName", updated.getName());
      assertEquals("tNew", updated.getMachineTypeUuid());
   }

   // ----------------------------
   // Delete machine
   // ----------------------------

   @Test
   void deleteMachine_whenExists_deletesSuccessfully() {
      when(machineRepository.existsByUuid("del1")).thenReturn(true);
      doNothing().when(machineRepository).deleteByUuid("del1");

      machineService.deleteMachine("del1");

      verify(machineRepository, times(1)).deleteByUuid("del1");
   }

   @Test
   void deleteMachine_whenMissing_throwsException() {
      when(machineRepository.existsByUuid("missing")).thenReturn(false);
      assertThrows(MachineNotFoundException.class, () -> machineService.deleteMachine("missing"));
      verify(machineRepository, never()).deleteByUuid(any());
   }

   // ----------------------------
   // Find operations
   // ----------------------------

   @Test
   void findByName_returnsOptional() {
      Machine m = new Machine("N", "d", new MachineType(), new MachineStatus());
      when(machineRepository.findByName("N")).thenReturn(Optional.of(m));

      Optional<Machine> result = machineService.findByName("N");

      assertTrue(result.isPresent());
   }

   @Test
   void findByType_returnsMappedDtos() {
      MachineType t = new MachineType();
      t.setMachineTypeUuid("t1");
      MachineStatus s = new MachineStatus();
      s.setMachineStatusUuid("s1");

      Machine m = new Machine("M", "d", t, s);
      m.setMachineUuid("m1");

      when(machineRepository.findByType(t)).thenReturn(List.of(m));

      List<MachineDto> results = machineService.findByType(t);

      assertEquals(1, results.size());
      assertEquals("m1", results.get(0).getMachineUuid());
   }

   @Test
   void findByStatus_returnsMappedDtos() {
      MachineType t = new MachineType();
      t.setMachineTypeUuid("t1");
      MachineStatus s = new MachineStatus();
      s.setMachineStatusUuid("s1");

      Machine m = new Machine("M", "d", t, s);
      m.setMachineUuid("m1");

      when(machineRepository.findByStatus(s)).thenReturn(List.of(m));

      List<MachineDto> results = machineService.findByStatus(s);

      assertEquals(1, results.size());
      assertEquals("m1", results.get(0).getMachineUuid());
   }

   @Test
   void findByTypeAndStatus_returnsMappedDtos() {
      MachineType t = new MachineType();
      t.setMachineTypeUuid("t1");
      MachineStatus s = new MachineStatus();
      s.setMachineStatusUuid("s1");

      Machine m = new Machine("M", "d", t, s);
      m.setMachineUuid("m1");

      when(machineRepository.findByTypeAndStatus(t, s)).thenReturn(List.of(m));

      List<MachineDto> results = machineService.findByTypeAndStatus(t, s);

      assertEquals(1, results.size());
      assertEquals("m1", results.get(0).getMachineUuid());
   }
}
