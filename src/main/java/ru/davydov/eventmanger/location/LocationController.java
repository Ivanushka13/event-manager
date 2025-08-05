package ru.davydov.eventmanger.location;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.davydov.eventmanger.location.mappers.LocationDtoMapper;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Slf4j
public class LocationController {

    private final LocationService locationService;
    private final LocationDtoMapper locationMapper;

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        log.info("Get request for get all locations");
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations.stream().map(locationMapper::toDto).toList());
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(
            @RequestBody @Valid LocationDto locationDto
    ) {
        log.info("Get request for location create: locationDto={}", locationDto);
        Location createdLocation = locationService.createLocation(locationMapper.toDomain(locationDto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(locationMapper.toDto(createdLocation));
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<LocationDto> deleteLocation(
            @PathVariable("locationId") Long locationId
    ) {
        log.info("Get request for location delete: locationId={}", locationId);
        Location deletedLocation = locationService.deleteLocation(locationId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(locationMapper.toDto(deletedLocation));
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationDto> getLocationById(
            @PathVariable("locationId") Long locationId
    ) {
        log.info("Get request for get location: locationId={}", locationId);
        Location foundLocation = locationService.getLocationById(locationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationMapper.toDto(foundLocation));
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable("locationId") Long locationId,
            @RequestBody @Valid LocationDto updateLocationDto
    ) {
        log.info("Get request for update location: locationId={}, updateLocationDto={}",
                locationId, updateLocationDto);

        Location updatedLocation = locationService.updateLocation(
                locationId,
                locationMapper.toDomain(updateLocationDto)
        );
        return ResponseEntity.ok(locationMapper.toDto(updatedLocation));
    }
}
