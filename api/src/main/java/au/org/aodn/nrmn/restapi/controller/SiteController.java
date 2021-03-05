package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.assembler.SiteListItemAssembler;
import au.org.aodn.nrmn.restapi.dto.site.SiteListItem;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@Tag(name = "sites")
public class SiteController {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SiteListItemAssembler assembler;

    @GetMapping(path = "/api/siteListItems")
    public CollectionModel<SiteListItem> list() {
        return CollectionModel.of(
                siteRepository.findAll()
                              .stream()
                              .map(site -> assembler.toModel(site))
                              .collect(Collectors.toList())
        );
    }

}
