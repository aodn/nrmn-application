package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.controller.assembler.SiteListItemAssembler;
import au.org.aodn.nrmn.restapi.controller.exception.DuplicateSiteException;
import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
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
        return ResponseEntity.ok(siteRepository.findAllSiteCodes().stream().collect(Collectors.toList()));
    }

    @GetMapping(path = "/siteStates")
    public ResponseEntity<List<String>> getAllSiteStates() {
        return ResponseEntity.ok(siteRepository.findAllSiteStates().stream().collect(Collectors.toList()));
    }


    @GetMapping(path = "/siteProvinces")
    public ResponseEntity<List<String>> getAllSiteProvinces() {
        return ResponseEntity.ok(siteRepository.findAllSiteProvinces().stream().collect(Collectors.toList()));
    }

    @GetMapping("/sites/{id}")
    public SiteGetDto findOne(@PathVariable Integer id) {
        Site site = siteRepository.findById(id)
                                  .orElseThrow(ResourceNotFoundException::new);
        return mapper.map(site, SiteGetDto.class);
    }

    @PostMapping("/sites")
    @ResponseStatus(HttpStatus.CREATED)
    public SiteDto newSite(@Valid @RequestBody SiteDto sitePostDto) {
        validatePost(sitePostDto);
        Site newSite = mapper.map(sitePostDto, Site.class);
        Site persistedSite = siteRepository.save(newSite);
        return mapper.map(persistedSite, SiteDto.class);
    }

    @PutMapping("/sites/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SiteDto updateSite(@PathVariable Integer id, @Valid @RequestBody SiteDto sitePutDto) {
        validatePut(id, sitePutDto);
        Site site = siteRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        mapper.map(sitePutDto, site);
        Site persistedSite = siteRepository.save(site);
        return mapper.map(persistedSite, SiteDto.class);
    }

    @DeleteMapping("/sites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Integer id) {
        siteRepository.deleteById(id);
    }

    private void validatePost(@RequestBody @Valid SiteDto sitePostDto) {
        Optional<Site> existingSite = getSiteWithCodeAndName(sitePostDto.getSiteCode(), sitePostDto.getSiteName());

        if (existingSite.isPresent()) {
            throw new DuplicateSiteException();
        }
    }

    private void validatePut(Integer id, SiteDto sitePutDto) {
        Optional<Site> existingSite = getSiteWithCodeAndName(sitePutDto.getSiteCode(), sitePutDto.getSiteName());

        if (existingSite.isPresent() && !existingSite.get().getSiteId().equals(id)) {
            throw new DuplicateSiteException();
        }
    }

    private Optional<Site> getSiteWithCodeAndName(String siteCode, String siteName) {
        Example<Site> siteWithCodeAndNameExample = Example.of(
                Site.builder()
                    .siteCode(siteCode)
                    .siteName(siteName)
                    .build());

        return siteRepository.findOne(siteWithCodeAndNameExample);
    }
}
