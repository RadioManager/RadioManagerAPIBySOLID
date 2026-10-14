package cs.vsu.radiomanagerapibysolid.controller;

import cs.vsu.radiomanagerapibysolid.dto.RadioStationDto;
import cs.vsu.radiomanagerapibysolid.service.inter.RadioStationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/radio-station")
@AllArgsConstructor
public class RadioStationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RadioStationController.class);

    private final RadioStationService radioStationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all radio stations",
            description = "Retrieves a list of all radio stations."
    )
    public ResponseEntity<?> getAllRadioStations() {
        LOGGER.info("Fetching all radio stations");
        List<RadioStationDto> stations = radioStationService.getAllRadioStation();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get radio station by ID",
            description = "Retrieves a radio station by its ID."
    )
    public ResponseEntity<?> getRadioStationById(@PathVariable Long id) {
        LOGGER.info("Fetching radio station with ID: {}", id);
        RadioStationDto station = radioStationService.getRadioStationById(id);
        if (station != null) {
            return ResponseEntity.ok(station);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/representative/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get radio station by representative ID",
            description = "Retrieves a radio station by its representative's ID."
    )
    public ResponseEntity<?> getRadioStationByRepresentativeId(@PathVariable Long id) {
        LOGGER.info("Fetching radio station with representative ID: {}", id);
        RadioStationDto station = radioStationService.getRadioStationByRepresentativeId(id);
        if (station != null) {
            return ResponseEntity.ok(station);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/by-name")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get radio station by name",
            description = "Retrieves a radio station by its name."
    )
    public ResponseEntity<?> getRadioStationByName(@RequestParam String name) {
        LOGGER.info("Fetching radio station by name: {}", name);
        RadioStationDto station = radioStationService.getRadioStationByName(name);
        if (station != null) {
            return ResponseEntity.ok(station);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/by-frequency")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get radio stations by frequency",
            description = "Retrieves a list of radio stations by the specified frequency."
    )
    public ResponseEntity<?> getRadioStationsByFrequency(@RequestParam BigDecimal frequency) {
        LOGGER.info("Fetching radio stations by frequency: {}", frequency);
        List<RadioStationDto> stations = radioStationService.getRadioStationsByFrequency(frequency);
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/by-city/{cityId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get radio stations by city ID",
            description = "Retrieves a list of radio stations located in the specified city."
    )
    public ResponseEntity<?> getRadioStationsByCityId(@PathVariable Long cityId) {
        LOGGER.info("Fetching radio stations by city ID: {}", cityId);
        List<RadioStationDto> stations = radioStationService.getRadioStationsByCityId(cityId);
        return ResponseEntity.ok(stations);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RADIO_REPRESENTATIVE', 'ADMIN')"
            + " and @accessService.isOwnerOrAdmin(#radioStationDto.representativeId)")
    @Operation(
            summary = "Create new radio station",
            description = "Creates a new radio station with the provided details."
    )
    public ResponseEntity<?> createRadioStation(@RequestBody @Valid RadioStationDto radioStationDto) {
        LOGGER.info("Creating radio station: {}", radioStationDto);
        RadioStationDto createdStation = radioStationService.createRadioStation(radioStationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStation);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('RADIO_REPRESENTATIVE', 'ADMIN')"
            + " and @accessService.isStationOwnerOrAdmin(#radioStationDto.id)"
            + " and @accessService.isOwnerOrAdmin(#radioStationDto.representativeId)")
    @Operation(
            summary = "Update radio station",
            description = "Updates an existing radio station with the provided details."
    )
    public ResponseEntity<?> updateRadioStation(@RequestBody @Valid RadioStationDto radioStationDto) {
        LOGGER.info("Updating radio station: {}", radioStationDto);
        RadioStationDto updatedStation = radioStationService.updateRadioStation(radioStationDto);
        if (updatedStation != null) {
            return ResponseEntity.ok(updatedStation);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete radio station",
            description = "Deletes the radio station with the specified ID."
    )
    public ResponseEntity<?> deleteRadioStation(@PathVariable Long id) {
        LOGGER.info("Deleting radio station with ID: {}", id);
        boolean deleted = radioStationService.deleteRadioStation(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
