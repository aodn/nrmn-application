package au.org.aodn.nrmn.restapi.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.validation.RowError;
import au.org.aodn.nrmn.restapi.dto.location.LocationDto;
import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import au.org.aodn.nrmn.restapi.repository.projections.LocationExtendedMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Locations")
@RequestMapping("/api")
public class LocationController {

    @Autowired
    private LocationRepository locationRepository;

    @GetMapping("/locations")
    public List<LocationExtendedMapping> getLocationsWithRegions() {
        List<LocationExtendedMapping> locations = locationRepository.getAllWithRegions();
        return locations;
    }

    @PostMapping("/location")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> saveLocation(@Valid @RequestBody Location locationDto) {
        Example<Location> example = Example.of(Location.builder().locationName(locationDto.getLocationName()).build());
        if (locationRepository.findOne(example).isPresent()) {
            RowError error = RowError.builder().property("locationName")
                    .message("A location with that name already exists.").build();
            return ResponseEntity.badRequest().body(List.of(error));
        }
        Location updatedLocation = locationRepository.save(locationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedLocation);
    }


    @GetMapping("/location/{id}")
    public ResponseEntity<?> getLocation(@PathVariable Integer id) {
        var res = locationRepository.getById(id);
        return ResponseEntity.ok(new LocationDto(res));
    }

    @PutMapping("/location/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> updateLocation(@PathVariable Integer id, @Valid @RequestBody Location locationDto) {
        Location location = locationRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (!location.getLocationName().contentEquals(locationDto.getLocationName())) {
            Example<Location> example = Example
                    .of(Location.builder().locationName(locationDto.getLocationName()).build());
            if (locationRepository.findOne(example).isPresent()) {
                RowError error = RowError.builder().property("locationName")
                        .message("A location with that name already exists.").build();
                return ResponseEntity.badRequest().body(List.of(error));
            }
        }
        location.setLocationName(locationDto.getLocationName());
        location.setIsActive(locationDto.getIsActive());
        Location updatedLocation = locationRepository.save(location);
        return ResponseEntity.ok().body(updatedLocation);
    }
}
