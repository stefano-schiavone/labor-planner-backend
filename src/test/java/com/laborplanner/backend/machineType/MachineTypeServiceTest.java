package com.laborplanner.backend.machineType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.laborplanner.backend.exception.machine.DuplicateMachineTypeNameException;
import com.laborplanner.backend.exception.machine.MachineTypeNotFoundException;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.repository.MachineTypeRepository;
import com.laborplanner.backend.service.MachineTypeService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MachineTypeServiceTest {

   @Mock
   private MachineTypeRepository machineTypeRepository;

   @InjectMocks
   private MachineTypeService machineTypeService;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
   }

   // ----------------------------
   // Get all
   // ----------------------------

   @Test
   void getAllTypes_returnsListOrderedByName() {
      MachineType t1 = new MachineType();
      t1.setMachineTypeUuid("t1");
      t1.setName("A");

      MachineType t2 = new MachineType();
      t2.setMachineTypeUuid("t2");
      t2.setName("B");

      when(machineTypeRepository.findAllByOrderByNameAsc()).thenReturn(List.of(t1, t2));

      List<MachineType> all = machineTypeService.getAllTypes();

      assertEquals(2, all.size());
      assertEquals("A", all.get(0).getName());
      assertEquals("B", all.get(1).getName());
   }

   // ----------------------------
   // Get by uuid
   // ----------------------------

   @Test
   void getTypeByUuid_whenExists_returnsType() {
      MachineType mt = new MachineType();
      mt.setMachineTypeUuid("u1");
      mt.setName("X");

      when(machineTypeRepository.findByUuid("u1")).thenReturn(Optional.of(mt));

      MachineType found = machineTypeService.getTypeByUuid("u1");

      assertEquals("u1", found.getMachineTypeUuid());
      assertEquals("X", found.getName());
   }

   @Test
   void getTypeByUuid_whenMissing_throwsException() {
      when(machineTypeRepository.findByUuid("missing")).thenReturn(Optional.empty());

      assertThrows(MachineTypeNotFoundException.class, () -> machineTypeService.getTypeByUuid("missing"));
   }

   // ----------------------------
   // Create
   // ----------------------------

   @Test
   void createType_withDuplicateName_throwsException() {
      MachineType mt = new MachineType();
      mt.setName("Lathe");

      when(machineTypeRepository.existsByName("Lathe")).thenReturn(true);

      assertThrows(DuplicateMachineTypeNameException.class, () -> machineTypeService.createType(mt));

      verify(machineTypeRepository, times(1)).existsByName("Lathe");
      verifyNoMoreInteractions(machineTypeRepository);
   }

   @Test
   void createType_success_createsType() {
      MachineType mt = new MachineType();
      mt.setName("CNC");

      MachineType saved = new MachineType();
      saved.setMachineTypeUuid("uuid-1");
      saved.setName("CNC");

      when(machineTypeRepository.existsByName("CNC")).thenReturn(false);
      when(machineTypeRepository.create(mt)).thenReturn(saved);

      MachineType created = machineTypeService.createType(mt);

      assertEquals("uuid-1", created.getMachineTypeUuid());
      assertEquals("CNC", created.getName());
   }

   // ----------------------------
   // Update
   // ----------------------------

   @Test
   void updateType_success_returnsUpdatedType() {
      MachineType existing = new MachineType();
      existing.setMachineTypeUuid("t99");
      existing.setName("Old");

      MachineType updated = new MachineType();
      updated.setName("New");

      MachineType storedAfterUpdate = new MachineType();
      storedAfterUpdate.setMachineTypeUuid("t99");
      storedAfterUpdate.setName("New");

      when(machineTypeRepository.findByUuid("t99")).thenReturn(Optional.of(existing));
      when(machineTypeRepository.update(existing)).thenReturn(storedAfterUpdate);

      MachineType result = machineTypeService.updateType("t99", updated);

      assertEquals("New", result.getName());
   }

   @Test
   void updateType_whenMissing_throwsException() {
      MachineType mt = new MachineType();
      mt.setName("Anything");

      when(machineTypeRepository.findByUuid("missing")).thenReturn(Optional.empty());

      assertThrows(MachineTypeNotFoundException.class, () -> machineTypeService.updateType("missing", mt));
   }

   // ----------------------------
   // Delete
   // ----------------------------

   @Test
   void deleteType_whenExists_deletesSuccessfully() {
      when(machineTypeRepository.existsByUuid("d1")).thenReturn(true);
      doNothing().when(machineTypeRepository).deleteByUuid("d1");

      machineTypeService.deleteType("d1");

      verify(machineTypeRepository, times(1)).deleteByUuid("d1");
   }

   @Test
   void deleteType_whenMissing_throwsException() {
      when(machineTypeRepository.existsByUuid("missing")).thenReturn(false);

      assertThrows(MachineTypeNotFoundException.class, () -> machineTypeService.deleteType("missing"));
      verify(machineTypeRepository, never()).deleteByUuid(any());
   }

   // ----------------------------
   // Find by name
   // ----------------------------

   @Test
   void findByName_delegatesToRepository() {
      MachineType mt = new MachineType();
      mt.setName("Lathe");

      when(machineTypeRepository.findByName("Lathe")).thenReturn(Optional.of(mt));

      Optional<MachineType> result = machineTypeService.findByName("Lathe");

      assertTrue(result.isPresent());
      assertEquals("Lathe", result.get().getName());
   }
}
