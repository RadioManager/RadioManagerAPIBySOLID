package cs.vsu.radiomanagerapibysolid.controller;

import cs.vsu.radiomanagerapibysolid.dto.BroadcastSlotDto;
import cs.vsu.radiomanagerapibysolid.model.enumerate.Status;
import cs.vsu.radiomanagerapibysolid.service.inter.BroadcastSlotService;
import cs.vsu.radiomanagerapibysolid.service.inter.DataTableService;
import cs.vsu.radiomanagerapibysolid.service.inter.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/broadcast-slot")
@AllArgsConstructor
public class BroadcastSlotController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastSlotController.class);

    private final BroadcastSlotService broadcastSlotService;

    private final DataTableService dataTableService;

    private final PricingService pricingService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all broadcast slots",
            description = "Retrieves a list of all broadcast slots."
    )
    public ResponseEntity<List<BroadcastSlotDto>> getAllBroadcastSlots() {
        LOGGER.info("Fetching all broadcast slots");
        List<BroadcastSlotDto> slots = broadcastSlotService.getAllBroadcastSlots();
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slot by ID",
            description = "Retrieves a broadcast slot by its ID."
    )
    public ResponseEntity<BroadcastSlotDto> getBroadcastSlotById(@PathVariable Long id) {
        LOGGER.info("Fetching broadcast slot with ID: {}", id);
        BroadcastSlotDto slot = broadcastSlotService.getBroadcastSlotById(id);
        if (slot != null) {
            return ResponseEntity.ok(slot);
        }

        LOGGER.warn("Broadcast slot with ID {} not found", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/by-time-and-station")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slot by start time, end time and radio station",
            description = "Retrieves a broadcast slot by its start time, end time and associated radio station ID."
    )
    public ResponseEntity<BroadcastSlotDto> getBroadcastSlotByStartEndAndStation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam Long radioStationId) {
        LOGGER.info("Fetching broadcast slot with startTime: {} and endTime: {} for radio station ID: {}",
                startTime, endTime, radioStationId);
        BroadcastSlotDto slot = broadcastSlotService.getBroadcastSlotByStartTimeAndEndTimeAndRadioStation(
                startTime, endTime, radioStationId);
        if (slot != null) {
            return ResponseEntity.ok(slot);
        }

        LOGGER.warn("Broadcast slot with startTime: {} and endTime: {} for radio station ID: {} not found",
                startTime, endTime, radioStationId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/by-status/{status}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slots by status",
            description = "Retrieves a list of broadcast slots filtered by their status."
    )
    public ResponseEntity<List<BroadcastSlotDto>> getBroadcastSlotsByStatus(@PathVariable String status) {
        LOGGER.info("Fetching broadcast slots with status: {}", status);
        Status slotStatus = Status.valueOf(status.toUpperCase());
        List<BroadcastSlotDto> slots = broadcastSlotService.getBroadcastSlotsByStatus(slotStatus);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/by-time")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slot by start and end time",
            description = "Retrieves a broadcast slot by its start time and end time."
    )
    public ResponseEntity<BroadcastSlotDto> getBroadcastSlotByStartEnd(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        LOGGER.info("Fetching broadcast slot with startTime: {} and endTime: {}", startTime, endTime);
        BroadcastSlotDto slot = broadcastSlotService.getBroadcastSlotByStartTimeAndEndTime(startTime, endTime);
        if (slot != null) {
            return ResponseEntity.ok(slot);
        }

        LOGGER.warn("Broadcast slot with startTime: {} and endTime: {} not found", startTime, endTime);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/station/{radioStationId}/after")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slots after a given start time for a radio station",
            description = "Retrieves broadcast slots for the specified radio station that start after the provided start time."
    )
    public ResponseEntity<List<BroadcastSlotDto>> getBroadcastSlotsAfterStartTime(
            @PathVariable Long radioStationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        LOGGER.info("Fetching broadcast slots for radio station {} after {}", radioStationId, startTime);
        List<BroadcastSlotDto> slots = broadcastSlotService.getBroadcastSlotsByRadioStationIdAfterStartTime(
                radioStationId, startTime);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/station/{radioStationId}/by-status-after")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slots by status after a given start time for a radio station",
            description = "Retrieves broadcast slots for the specified radio station that start after the given time and have the specified status."
    )
    public ResponseEntity<List<BroadcastSlotDto>> getBroadcastSlotsByStatusAfterStartTime(
            @PathVariable Long radioStationId,
            @RequestParam String status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        LOGGER.info("Fetching broadcast slots for radio station {} with status {} after {}",
                radioStationId, status, startTime);
        Status slotStatus = Status.valueOf(status.toUpperCase());
        List<BroadcastSlotDto> slots = broadcastSlotService.getBroadcastSlotsByRadioStationIdWithStatusAfterStartTime(
                radioStationId, slotStatus, startTime);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/station/{radioStationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all slots for a radio station",
            description = "Retrieves all broadcast slots (any status) for the specified radio station ID."
    )
    public ResponseEntity<List<BroadcastSlotDto>> getBroadcastSlotsByRadioStationId(@PathVariable Long radioStationId) {
        LOGGER.info("Fetching all slots for radio station with id: {}", radioStationId);
        List<BroadcastSlotDto> slots = broadcastSlotService.getBroadcastSlotByRadioStationId(radioStationId);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/station/{radioStationId}/status/{status}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get slots by station and status",
            description = "Retrieves all broadcast slots with the given status for the specified radio station ID."
    )
    public ResponseEntity<List<BroadcastSlotDto>> getBroadcastSlotsByStatusAndRadioStationId(
            @PathVariable Long radioStationId,
            @PathVariable String status) {
        LOGGER.info("Fetching slots for station {} with status: {}", radioStationId, status);
        Status slotStatus = Status.valueOf(status.toUpperCase());
        List<BroadcastSlotDto> slots = broadcastSlotService.getBroadcastSlotsByRadioStationIdWithStatus(
                radioStationId, slotStatus);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/station/{radioStationId}/available")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get available slots by priority for a station",
            description = "Retrieves only AVAILABLE slots for the given radio station ID, filtered by priority window. " +
                    "Pass highPriority=true to get slots in the high-priority windows, false for the rest."
    )
    public ResponseEntity<List<BroadcastSlotDto>> getAvailableSlotsByRadioStationIdWithPriority(
            @PathVariable Long radioStationId, @RequestParam boolean highPriority) {
        LOGGER.info("Fetching AVAILABLE slots for station {} with high priority: {}", radioStationId, highPriority);
        List<BroadcastSlotDto> slots = broadcastSlotService.getEmptyBroadcastSlotsByPriorityWithRadioStation(
                radioStationId, highPriority);
        return ResponseEntity.ok(slots);
    }

    @PostMapping("/excel")
    @PreAuthorize("hasAnyRole('RADIO_REPRESENTATIVE', 'ADMIN')"
            + " and @accessService.isStationOwnerOrAdmin(#radioStationId)")
    @Operation(
            summary = "Bulk create broadcast slots from Excel file",
            description = "Reads an Excel (.xlsx) file containing start and end times in" +
                    " the first two columns and replaces future broadcast slots of the specified radio station."
    )
    public ResponseEntity<List<BroadcastSlotDto>> createSlotsFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("radioStationId") Long radioStationId) {
        LOGGER.info("Creating broadcast slots for station {} from file: {}", radioStationId, file.getOriginalFilename());
        List<BroadcastSlotDto> slots = broadcastSlotService.replaceFutureSlots(
                radioStationId, dataTableService.getTimeFromTable(file));
        return ResponseEntity.ok(slots);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create broadcast slot",
            description = "Creates a new broadcast slot."
    )
    public ResponseEntity<BroadcastSlotDto> createBroadcastSlot(@RequestBody @Valid BroadcastSlotDto broadcastSlotDto) {
        LOGGER.info("Creating broadcast slot: {}", broadcastSlotDto);
        BroadcastSlotDto created = broadcastSlotService.createBroadcastSlot(broadcastSlotDto);
        return ResponseEntity.ok(created);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update broadcast slot",
            description = "Updates an existing broadcast slot."
    )
    public ResponseEntity<BroadcastSlotDto> updateBroadcastSlot(@RequestBody @Valid BroadcastSlotDto broadcastSlotDto) {
        LOGGER.info("Updating broadcast slot: {}", broadcastSlotDto);
        BroadcastSlotDto updated = broadcastSlotService.updateBroadcastSlot(broadcastSlotDto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }

        LOGGER.warn("Broadcast slot with ID {} not found for update", broadcastSlotDto.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete broadcast slot",
            description = "Deletes a broadcast slot by its ID."
    )
    public ResponseEntity<Void> deleteBroadcastSlot(@PathVariable Long id) {
        LOGGER.info("Deleting broadcast slot with ID: {}", id);
        boolean deleted = broadcastSlotService.deleteBroadcastSlot(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }

        LOGGER.warn("Broadcast slot with ID {} not found for deletion", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/station/{radioStationId}/after")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete broadcast slots for a radio station after a given start time",
            description = "Deletes all broadcast slots for the specified radio station that start after the provided start time."
    )
    public ResponseEntity<Void> deleteBroadcastSlotsAfterStartTime(
            @PathVariable Long radioStationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        LOGGER.info("Deleting broadcast slots for radio station {} after {}", radioStationId, startTime);
        boolean deleted = broadcastSlotService.deleteBroadcastSlotsByRadioStationAfterStartTime(radioStationId, startTime);
        if (deleted) {
            return ResponseEntity.ok().build();
        }

        LOGGER.warn("No broadcast slots found to delete for radio station {} after {}", radioStationId, startTime);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update broadcast slot status",
            description = "Updates the status of a broadcast slot by its ID. Valid statuses are defined in the Status enumeration."
    )
    public ResponseEntity<BroadcastSlotDto> updateBroadcastSlotStatus(@PathVariable Long id, @RequestParam String status) {
        LOGGER.info("Updating status of broadcast slot {} to {}", id, status);
        Status newStatus = Status.valueOf(status.toUpperCase());
        BroadcastSlotDto updated = broadcastSlotService.updateBroadcastSlotStatus(id, newStatus);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }

        LOGGER.warn("Broadcast slot with ID {} not found for status update", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}/end-time")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update broadcast slot end time",
            description = "Updates the end time of a broadcast slot by its ID."
    )
    public ResponseEntity<BroadcastSlotDto> updateBroadcastSlotEndTime(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        LOGGER.info("Updating end time of broadcast slot {} to {}", id, endTime);
        BroadcastSlotDto updated = broadcastSlotService.updateBroadcastSlotEndTime(id, endTime);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }

        LOGGER.warn("Broadcast slot with ID {} not found for end time update", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/split")
    @PreAuthorize("@accessService.isRecordingOwnerOrAdmin(#audioId)")
    @Operation(
            summary = "Split broadcast slot",
            description = "Splits an existing broadcast slot based on the duration of the attached audio recording. " +
                    "The original slot's end time is updated, and a new slot is created for the remaining period."
    )
    public ResponseEntity<BroadcastSlotDto> splitBroadcastSlot(@RequestParam Long slotId, @RequestParam Long audioId) {
        LOGGER.info("Splitting broadcast slot {} by duration of audio recording {}", slotId, audioId);
        BroadcastSlotDto updatedSlot = broadcastSlotService.splitBroadcastSlotByAudio(slotId, audioId);
        return ResponseEntity.ok(updatedSlot);
    }

    @GetMapping("/by-month")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slots by month",
            description = "Retrieves broadcast slots for a specific year and month."
    )
    public ResponseEntity<List<BroadcastSlotDto>> getBroadcastSlotsByMonth(@RequestParam int year, @RequestParam int month) {
        LOGGER.info("Fetching broadcast slots for year: {} and month: {}", year, month);
        List<BroadcastSlotDto> slots = broadcastSlotService.getBroadcastSlotsByMonth(year, month);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/statuses")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get available statuses for slot",
            description = "Retrieves a list of all available statuses for slots." +
                    " This is useful for populating status selection dropdowns in the UI."
    )
    public ResponseEntity<List<Status>> getStatuses() {
        LOGGER.info("Fetching available statuses for slot");
        List<Status> statuses = Arrays.asList(Status.values());
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/priority-multiplier")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get priority multiplier",
            description = "Returns the multiplier factor for the given priority flag (high priority or normal)."
    )
    public ResponseEntity<Double> getPriorityMultiplier(@RequestParam boolean highPriority) {
        LOGGER.info("Fetching priority multiplier for highPriority={}", highPriority);
        double multiplier = pricingService.getPriorityMultiplier(highPriority);
        return ResponseEntity.ok(multiplier);
    }

}
