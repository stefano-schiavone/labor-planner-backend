package com.laborplanner.backend.controller;

import com.laborplanner.backend.dto.machine.MachineTypeRequest;
import com.laborplanner.backend.dto.machine.MachineTypeResponse;
import com.laborplanner.backend.model.MachineType;
import com.laborplanner.backend.service.MachineTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/machine-types")
@Tag(name = "Machine Types", description = "Manage machine type definitions")
public class MachineTypeController {

  private final MachineTypeService machineTypeService;

  public MachineTypeController(MachineTypeService machineTypeService) {
    this.machineTypeService = machineTypeService;
  }

  @Operation(summary = "Get all machine types")
  @GetMapping
  public List<MachineTypeResponse> getAllTypes() {
    return machineTypeService.getAllTypes().stream()
        .map(t -> new MachineTypeResponse(t.getMachineTypeUuid(), t.getName()))
        .collect(Collectors.toList());
  }

  @Operation(summary = "Get a machine type by UUID")
  @GetMapping("/{uuid}")
  public MachineTypeResponse getType(@PathVariable String uuid) {
    MachineType type = machineTypeService.getTypeByUuid(uuid);
    return new MachineTypeResponse(type.getMachineTypeUuid(), type.getName());
  }

  @Operation(summary = "Create a new machine type")
  @PostMapping
  public ResponseEntity<MachineTypeResponse> createType(
      @RequestBody @Valid MachineTypeRequest request) {
    MachineType created = machineTypeService.createType(new MachineType(request.getName()));
    MachineTypeResponse response =
        new MachineTypeResponse(created.getMachineTypeUuid(), created.getName());

    URI location = URI.create("/api/machine-types/" + created.getMachineTypeUuid());
    return ResponseEntity.created(location).body(response);
  }

  @Operation(summary = "Update a machine type")
  @PutMapping("/{uuid}")
  public MachineTypeResponse updateType(
      @PathVariable String uuid, @RequestBody @Valid MachineTypeRequest request) {
    MachineType updated = machineTypeService.updateType(uuid, new MachineType(request.getName()));
    return new MachineTypeResponse(updated.getMachineTypeUuid(), updated.getName());
  }

  @Operation(summary = "Delete a machine type")
  @DeleteMapping("/{uuid}")
  public void deleteType(@PathVariable String uuid) {
    machineTypeService.deleteType(uuid);
  }
}
