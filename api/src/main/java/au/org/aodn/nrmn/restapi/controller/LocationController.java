package au.org.aodn.nrmn.restapi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import au.org.aodn.nrmn.restapi.data.model.Location;
import au.org.aodn.nrmn.restapi.data.model.LocationListView;
import au.org.aodn.nrmn.restapi.data.repository.LocationListRepository;
import au.org.aodn.nrmn.restapi.data.repository.LocationRepository;
import au.org.aodn.nrmn.restapi.data.repository.dynamicQuery.FilterCondition;
import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.validation.RowError;
import au.org.aodn.nrmn.restapi.dto.location.LocationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Reference Data - Locations")
@RequestMapping("/api/v1")
public class LocationController {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationListRepository locationListRepository;

    @Autowired
    private ObjectMapper objMapper;

    @GetMapping("/locations")
    public ResponseEntity<?> getLocationsWithFilters(@RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "all", defaultValue = "false") Boolean all,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "100") int pageSize) throws JsonProcessingException {

        if (all) {
            return ResponseEntity.ok(locationListRepository.findAll());
        }

        // RequestParam do not support json object parsing automatically
        List<Filter> f = FilterCondition.parse(objMapper, filters, Filter[].class);
        List<Sorter> s = FilterCondition.parse(objMapper, sort, Sorter[].class);

        Page<LocationListView> v = locationListRepository.findAllLocationBy(f, s, PageRequest.of(page, pageSize));
        Map<String, Object> data = new HashMap<>();

        data.put("lastRow", v.getTotalElements());
        data.put("items", v.getContent());

        return ResponseEntity.ok(data);
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
        var lOptional = locationRepository.findById(id);
        if (!lOptional.isPresent())
            return ResponseEntity.notFound().build();
        var location = lOptional.get();
        return ResponseEntity.ok(new LocationDto(location));
    }

    @PutMapping("/location/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> updateLocation(@PathVariable Integer id, @Valid @RequestBody Location locationDto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserId " + id + " not found"));

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
