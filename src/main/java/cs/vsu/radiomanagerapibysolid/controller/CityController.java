package cs.vsu.radiomanagerapibysolid.controller;

import cs.vsu.radiomanagerapibysolid.dto.CityDto;
import cs.vsu.radiomanagerapibysolid.service.inter.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city")
@Tag(name = "Cities", description = "Cities of radio stations")
@AllArgsConstructor
public class CityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CityController.class);

    private final CityService cityService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all cities", description = "Retrieves a list of all cities")
    public ResponseEntity<List<CityDto>> getAllCities() {
        LOGGER.info("Fetching all cities");
        List<CityDto> cities = cityService.getAllCity();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get city by ID", description = "Retrieves a city by its ID")
    public ResponseEntity<CityDto> getCityById(@PathVariable Long id) {
        LOGGER.info("Fetching city by ID: {}", id);
        CityDto city = cityService.getCityById(id);
        if (city != null) {
            return ResponseEntity.ok(city);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/by-name-and-region")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get city by name and region", description = "Retrieves a city by its name and region")
    public ResponseEntity<CityDto> getCityByNameAndRegion(@RequestParam String name, @RequestParam String region) {
        LOGGER.info("Fetching city by name: {} and region: {}", name, region);
        CityDto city = cityService.getCityByNameAndRegion(name, region);
        if (city != null) {
            return ResponseEntity.ok(city);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/by-region/{region}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get cities by region", description = "Retrieves a list of cities by region")
    public ResponseEntity<List<CityDto>> getCitiesByRegion(@PathVariable String region) {
        LOGGER.info("Fetching cities by region: {}", region);
        List<CityDto> cities = cityService.getCityByRegion(region);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/by-name/{name}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get cities by name", description = "Retrieves a list of cities by name")
    public ResponseEntity<List<CityDto>> getCitiesByName(@PathVariable String name) {
        LOGGER.info("Fetching cities by name: {}", name);
        List<CityDto> cities = cityService.getCityByName(name);
        return ResponseEntity.ok(cities);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new city", description = "Creates a new city")
    public ResponseEntity<CityDto> createCity(@RequestBody @Valid CityDto cityDto) {
        LOGGER.info("Creating city: {}", cityDto);
        CityDto createdCity = cityService.createCity(cityDto);
        return ResponseEntity.ok(createdCity);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing city", description = "Updates a city")
    public ResponseEntity<CityDto> updateCity(@RequestBody @Valid CityDto cityDto) {
        LOGGER.info("Updating city: {}", cityDto);
        CityDto updatedCity = cityService.updateCity(cityDto);
        if (updatedCity != null) {
            return ResponseEntity.ok(updatedCity);
        }
        LOGGER.warn("City with ID {} not found for update", cityDto.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a city", description = "Deletes a city by its ID")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        LOGGER.info("Deleting city with ID: {}", id);
        boolean deleted = cityService.deleteCity(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        LOGGER.warn("City with ID {} not found for delete", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
