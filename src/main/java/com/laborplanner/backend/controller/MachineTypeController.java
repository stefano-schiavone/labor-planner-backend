package com.laborplanner.backend.controller;

import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.service.MachineTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/machine-types")
@Tag(name = "Machine Types", description = "Manage machine type definitions")
public class MachineTypeController {

  @Autowired private MachineTypeService machineTypeService;

  @Operation(summary = "Get all machine types")
  @GetMapping
  public List<MachineType> getAllTypes() {
    return machineTypeService.getAllTypes();
  }

  @Operation(summary = "Get a machine type by UUID")
  @GetMapping("/{uuid}")
  public MachineType getType(@PathVariable String uuid) {
    return machineTypeService.getTypeByUuid(uuid);
  }

  @Operation(summary = "Create a new machine type")
  @PostMapping
  public MachineType createType(@RequestBody MachineType type) {
    return machineTypeService.createType(type);
  }

  @Operation(summary = "Update a machine type")
  @PutMapping("/{uuid}")
  public MachineType updateType(@PathVariable String uuid, @RequestBody MachineType updatedType) {
    return machineTypeService.updateType(uuid, updatedType);
  }

  @Operation(summary = "Delete a machine type")
  @DeleteMapping("/{uuid}")
  public void deleteType(@PathVariable String uuid) {
    machineTypeService.deleteType(uuid);
  }
}
