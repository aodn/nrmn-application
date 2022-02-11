package au.org.aodn.nrmn.restapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import au.org.aodn.nrmn.restapi.repository.projections.LocationExtendedMapping;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Locations")
@RequestMapping("/api")
public class LocationController {

    @Autowired
    private LocationRepository locationRepository;
    
    @GetMapping("/locationList")
    public List<LocationExtendedMapping> getLocationsWithRegions() {
        List<LocationExtendedMapping> locations = locationRepository.getAllWithRegions();
        return locations;
    }
}
