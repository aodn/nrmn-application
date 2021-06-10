package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.service.model.SpeciesRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class WormsService {

    static private Pattern REMOVE_TRAILING_JUNK_PATTERN =
            Pattern.compile("( (sp\\.|spp\\.))?( (\\(.*\\)|\\[.*\\]))?$", Pattern.CASE_INSENSITIVE);

    private final WebClient wormsClient;

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    public WormsService(@Qualifier("wormsClient") WebClient wormsClient) {
        this.wormsClient = wormsClient;
    }

    public List<SpeciesRecord> partialSearch(int page, String searchTerm) {
        if (page > 0) return Collections.emptyList();

        Mono<SpeciesRecord[][]> response = wormsClient
                .get().uri(uriBuilder ->
                        uriBuilder.path("/AphiaRecordsByNames")
                                  .queryParam("scientificnames[]", "%" + removeTrailingJunk(searchTerm) + "%")
                                  .build())
                .retrieve()
                .bodyToMono(SpeciesRecord[][].class);
        SpeciesRecord[][] matchingSpecies = Optional.ofNullable(response.block())
                                                   .orElse(new SpeciesRecord[1][0]);

        return Arrays.stream(matchingSpecies[0])
                     .map(m -> {
                        m.setIsPresent(m.getScientificName() != null ? observableItemRepository.count(Example.of(ObservableItem.builder().observableItemName(m.getScientificName()).build())) > 0 : false);
                        return m;
                    })
                     .collect(Collectors.toList());
    }

    static String removeTrailingJunk(String searchTerm) {
        return REMOVE_TRAILING_JUNK_PATTERN.matcher(searchTerm).replaceAll("");
    }
}
