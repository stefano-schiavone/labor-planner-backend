package com.laborplanner.backend.controller;

import com.laborplanner.backend.dto.machine.MachineDto;
import com.laborplanner.backend.dto.machine.MachineRequest;
import com.laborplanner.backend.dto.machine.MachineResponse;
import com.laborplanner.backend.service.MachineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Structure:
 * Every method in this controller works like this
 * - Takes in some sort of request
 * - Converts to a Dto
 * - Retrieves a Dto
 * - Sends a response
 */

@RestController
@RequestMapping("/api/machines")
@Tag(name = "Machines", description = "Manage machine definitions")
public class MachineController {

  private MachineService machineService;

  public MachineController(MachineService machineService) {
    this.machineService = machineService;
  }

  @Operation(summary = "Get all machines")
  @GetMapping
  public List<MachineResponse> getAllMachines() {
    return machineService.getAllMachines().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Operation(summary = "Get a machine by UUID")
  @GetMapping("/{uuid}")
  public MachineResponse getMachine(@PathVariable String uuid) {
    return toResponse(machineService.getMachineByUuid(uuid));
  }

  @Operation(summary = "Create a new machine")
  @PostMapping
  public ResponseEntity<MachineResponse> createMachine(@RequestBody @Valid MachineRequest request) {
    MachineDto machine = toDto(request);

    // Receive result from service
    MachineDto created = machineService.createMachine(machine);
    // Turn into response
    MachineResponse response = toResponse(created);

    URI location = URI.create("/api/machines/" + created.getMachineUuid());
    return ResponseEntity.created(location).body(response);
  }

  @Operation(summary = "Update a machine")
  @PutMapping("/{uuid}")
  public MachineResponse updateMachine(
      @PathVariable String uuid, @RequestBody @Valid MachineRequest request) {
    MachineDto updated = toDto(request);

    MachineResponse savedResponse = toResponse(machineService.updateMachine(uuid, updated));
    // Spring automatically responds with 200 without me having to say it
    return savedResponse;
  }

  @Operation(summary = "Delete a machine")
  @DeleteMapping("/{uuid}")
  public void deleteMachine(@PathVariable String uuid) {
    machineService.deleteMachine(uuid);
  }

  // Helper to map dto → Response
  private MachineResponse toResponse(MachineDto dto) {
    return new MachineResponse(
        dto.getMachineUuid(),
        dto.getName(),
        dto.getDescription(),
        dto.getMachineTypeUuid(),
        dto.getMachineStatusUuid());
  }

  // Helper Response -> Dto
  private MachineDto toDto(MachineRequest req) {
    return new MachineDto(
        req.getName(), req.getDescription(), req.getMachineTypeUuid(), req.getMachineStatusUuid());
  }
}
