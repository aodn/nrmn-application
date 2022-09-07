package au.org.aodn.nrmn.restapi.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
import au.org.aodn.nrmn.restapi.model.db.SiteListView;
import au.org.aodn.nrmn.restapi.repository.*;
import au.org.aodn.nrmn.restapi.repository.dynamicQuery.FilterCondition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationError;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationErrors;
import au.org.aodn.nrmn.restapi.dto.site.SiteDto;
import au.org.aodn.nrmn.restapi.dto.site.SiteGetDto;
import au.org.aodn.nrmn.restapi.dto.site.SiteOptionsDto;
import au.org.aodn.nrmn.restapi.model.db.Site;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Reference Data - Sites")
@RequestMapping(path = "/api/v1")
public class SiteController {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private MarineProtectedAreaRepository marineProtectedAreaRepository;

    @Autowired
    private ProtectionStatusRepository protectionStatusRepository;

    @Autowired
    private SiteListRepository siteListRepository;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("/sites")
    public ResponseEntity<?> getSitesWithFilters(@RequestParam(value = "sort", required = false) String sort,
                                                 @RequestParam(value = "filters", required = false) String filters,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "pageSize", defaultValue = "100") int pageSize) throws JsonProcessingException {

        // RequestParam do not support json object parsing automatically
        List<Filter> f = FilterCondition.parse(objMapper, filters, Filter[].class);
        List<Sorter> s = FilterCondition.parse(objMapper, sort, Sorter[].class);

        Page<SiteListView> v = siteListRepository.findSiteBy(f, s, PageRequest.of(page, pageSize));
        Map<String, Object> data = new HashMap<>();

        data.put("lastRow", v.getTotalElements());
        data.put("items", v.getContent());

        return ResponseEntity.ok(data);
    }

    @GetMapping("/siteOptions")
    public ResponseEntity<SiteOptionsDto> getSiteOptions() {
        var siteOptions = new SiteOptionsDto();
        siteOptions.setLocations(locationRepository.findAll());
        siteOptions.setMarineProtectedAreas(marineProtectedAreaRepository.findAll().stream().map(mpa -> mpa.getName())
                .collect(Collectors.toList()));
        siteOptions.setProtectionStatuses(
                protectionStatusRepository.findAll().stream().map(ps -> ps.getName()).collect(Collectors.toList()));
        siteOptions.setSiteStates(siteRepository.findAllSiteStates());
        siteOptions.setSiteCountries(siteRepository.findAllCountries());
        return ResponseEntity.ok(siteOptions);
    }

    @GetMapping("/sitesAroundLocation")
    List<String> getSiteAroundLocation(@RequestParam(required = false) Integer exclude,
            @RequestParam(required = true) String latitude, @RequestParam(required = true) String longitude) {
        return siteRepository.sitesWithin200m(exclude != null ? exclude : -1, Double.parseDouble(longitude),
                Double.parseDouble(latitude));
    }

    @GetMapping("/site/{id}")
    public SiteGetDto findOne(@PathVariable Integer id) {
        Site site = siteRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return mapper.map(site, SiteGetDto.class);
    }

    @PostMapping("/site")
    public ResponseEntity<?> newSite(@Valid @RequestBody SiteDto sitePostDto) {
        Site newSite = mapper.map(sitePostDto, Site.class);
        ValidationErrors errors = validateConstraints(newSite);
        if (!errors.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Site persistedSite = siteRepository.save(newSite);
        SiteDto persistedSiteDto = mapper.map(persistedSite, SiteDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(persistedSiteDto);
    }

    @PutMapping("/site/{id}")
    public ResponseEntity<?> updateSite(@PathVariable Integer id, @Valid @RequestBody SiteDto sitePutDto) {
        Site site = siteRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        mapper.map(sitePutDto, site);
        ValidationErrors errors = validateConstraints(site);
        if (!errors.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Site persistedSite = siteRepository.save(site);
        SiteDto updatedSiteDto = mapper.map(persistedSite, SiteDto.class);
        return ResponseEntity.ok(updatedSiteDto);
    }

    @DeleteMapping("/site/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Integer id) {
        siteRepository.deleteById(id);
    }

    private ValidationErrors validateConstraints(Site site) {
        List<ValidationError> errors = new ArrayList<>();

        if (StringUtils.isBlank(site.getSiteName()))
            errors.add(new ValidationError("Site", "siteName", "", "Site Name must not be empty."));

        if (StringUtils.isBlank(site.getSiteCode()))
            errors.add(new ValidationError("Site", "siteCode", "", "Site Code must not be empty."));

        if (site.getLocation() == null)
            errors.add(new ValidationError("Site", "locationId", "", "Please select a location."));

        if (errors.size() > 0)
            return new ValidationErrors(errors);

        Example<Site> siteWithCodeExample = Example.of(
                Site.builder()
                        .siteCode(site.getSiteCode())
                        .build());

        Optional<Site> existingSite = siteRepository.findOne(siteWithCodeExample);

        if (existingSite.isPresent() && !existingSite.get().getSiteId().equals(site.getSiteId())) {
            errors.add(new ValidationError("Site", "siteCode", site.getSiteCode(),
                    "A site with this code already exists."));
        }

        Example<Site> siteWithLocationAndNameExample = Example.of(
                Site.builder()
                        .location(site.getLocation())
                        .siteName(site.getSiteName())
                        .build());

        Optional<Site> existingSiteWithName = siteRepository.findOne(siteWithLocationAndNameExample);

        if (existingSiteWithName.isPresent() && (site.getSiteCode() == null
                || !existingSiteWithName.get().getSiteCode().equalsIgnoreCase(site.getSiteCode()))) {
            errors.add(new ValidationError("Site", "siteName", site.getSiteName(),
                    "A site with this name already exists in this location."));
        }

        return new ValidationErrors(errors);
    }
}
