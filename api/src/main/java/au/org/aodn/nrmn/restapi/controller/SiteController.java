package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.controller.assembler.SiteListItemAssembler;
import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationError;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationErrors;
import au.org.aodn.nrmn.restapi.dto.site.SiteDto;
import au.org.aodn.nrmn.restapi.dto.site.SiteGetDto;
import au.org.aodn.nrmn.restapi.dto.site.SiteListItem;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Tag(name = "sites")
@RequestMapping(path = "/api")
public class SiteController {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private SiteListItemAssembler assembler;

    @GetMapping("/siteListItems")
    public CollectionModel<SiteListItem> list() {
        return CollectionModel.of(
                siteRepository.findAll(Sort.by("siteCode"))
                              .stream()
                              .map(site -> assembler.toModel(site))
                              .collect(Collectors.toList())
        );
    }

    @GetMapping("/sites")
    List<Site> findAll() {
        return siteRepository.findAll();
    }

    @GetMapping(path = "/siteCodes")
    public ResponseEntity<List<String>> getAllSiteAreas() {
        return ResponseEntity.ok(siteRepository.findAllSiteCodes());
    }

    @GetMapping(path = "/siteStates")
    public ResponseEntity<List<String>> getAllSiteStates() {
        return ResponseEntity.ok(siteRepository.findAllSiteStates());
    }


    @GetMapping(path = "/siteProvinces")
    public ResponseEntity<List<String>> getAllSiteProvinces() {
        return ResponseEntity.ok(siteRepository.findAllSiteProvinces());
    }

    @GetMapping("/sites/{id}")
    public SiteGetDto findOne(@PathVariable Integer id) {
        Site site = siteRepository.findById(id)
                                  .orElseThrow(ResourceNotFoundException::new);
        return mapper.map(site, SiteGetDto.class);
    }

    @PostMapping("/sites")
    public ResponseEntity newSite(@Valid @RequestBody SiteDto sitePostDto) {
        Site newSite = mapper.map(sitePostDto, Site.class);
        ValidationErrors errors = validateConstraints(newSite);
        if (!errors.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Site persistedSite = siteRepository.save(newSite);
        SiteDto updatedSiteDto = mapper.map(persistedSite, SiteDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedSiteDto);
    }

    @PutMapping("/sites/{id}")
    public ResponseEntity updateSite(@PathVariable Integer id, @Valid @RequestBody SiteDto sitePutDto) {
        Site site = siteRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        mapper.map(sitePutDto, site);
        ValidationErrors errors = validateConstraints(site);
        if (!errors.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Site persistedSite = siteRepository.save(site);
        SiteDto updatedSiteDto = mapper.map(persistedSite, SiteDto.class);
        return ResponseEntity.ok().body(updatedSiteDto);
    }

    @DeleteMapping("/sites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Integer id) {
        siteRepository.deleteById(id);
    }

    private ValidationErrors validateConstraints(Site site) {
        List<ValidationError> errors = new ArrayList<>();
        
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
        
        if (existingSiteWithName.isPresent() && !existingSiteWithName.get().getSiteId().equals(site.getSiteId())) {
            errors.add(new ValidationError("Site", "siteName", site.getSiteName(), 
            "A site with this name already exists in this location."));
        }

        return new ValidationErrors(errors);
    }

}
