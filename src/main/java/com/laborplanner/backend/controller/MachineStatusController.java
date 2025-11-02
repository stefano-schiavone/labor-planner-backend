package com.laborplanner.backend.controller;

import com.laborplanner.backend.dto.machine.MachineStatusRequest;
import com.laborplanner.backend.dto.machine.MachineStatusResponse;
import com.laborplanner.backend.model.MachineStatus;
import com.laborplanner.backend.service.MachineStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/machine-statuses")
@Tag(name = "Machine Statuses", description = "Manage machine status definitions")
public class MachineStatusController {

  @Autowired private MachineStatusService machineStatusService;

  @Operation(summary = "Get all machine statuses")
  @GetMapping
  public List<MachineStatus> getAllStatuses() {
    return machineStatusService.getAllStatuses();
  }

  @Operation(summary = "Get a machine status by UUID")
  @GetMapping("/{uuid}")
  public MachineStatus getStatus(@PathVariable String uuid) {
    return machineStatusService.getStatusByUuid(uuid);
  }

  @Operation(summary = "Create a new machine status")
  @PostMapping
  public ResponseEntity<MachineStatusResponse> createStatus(
      @RequestBody @Valid MachineStatusRequest request) {
    MachineStatus created = machineStatusService.createStatus(new MachineStatus(request.getName()));
    MachineStatusResponse response =
        new MachineStatusResponse(created.getMachineStatusUuid(), created.getName());

    // Return 201 Created with the Location header
    URI location = URI.create("/api/machine-statuses/" + created.getMachineStatusUuid());
    return ResponseEntity.created(location).body(response);
  }

  @Operation(summary = "Update a machine status")
  @PutMapping("/{uuid}")
  public MachineStatus updateStatus(
      @PathVariable String uuid, @RequestBody MachineStatus updatedStatus) {
    return machineStatusService.updateStatus(uuid, updatedStatus);
  }

  @Operation(summary = "Delete a machine status")
  @DeleteMapping("/{uuid}")
  public void deleteStatus(@PathVariable String uuid) {
    machineStatusService.deleteStatus(uuid);
  }
}
