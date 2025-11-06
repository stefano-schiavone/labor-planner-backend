package com.laborplanner.backend.controller;

import com.laborplanner.backend.model.Machine;
import com.laborplanner.backend.service.MachineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/machines")
@Tag(name = "Machines", description = "Manage machine definitions")
public class MachineController {

  @Autowired private MachineService machineService;

  // TODO: Check when creating machines if at least one machineType exists
  // TODO: Add check so that it cna't delete machine type if machines are assigned to that type
  @Operation(summary = "Get all machines")
  @GetMapping
  public List<Machine> getAllMachines() {
    return machineService.getAllMachines();
  }

  @Operation(summary = "Get a machine by UUID")
  @GetMapping("/{uuid}")
  public Machine getMachine(@PathVariable String uuid) {
    return machineService.getMachineByUuid(uuid);
  }

  @Operation(summary = "Create a new machine")
  @PostMapping
  public Machine createMachine(@RequestBody Machine machine) {
    return machineService.createMachine(machine);
  }

  @Operation(summary = "Update a machine")
  @PutMapping("/{uuid}")
  public Machine updateMachine(@PathVariable String uuid, @RequestBody Machine updatedMachine) {
    return machineService.updateMachine(uuid, updatedMachine);
  }

  @Operation(summary = "Delete a machine")
  @DeleteMapping("/{uuid}")
  public void deleteMachine(@PathVariable String uuid) {
    machineService.deleteMachine(uuid);
  }
}
