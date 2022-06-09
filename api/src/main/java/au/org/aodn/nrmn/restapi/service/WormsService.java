package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.service.model.SpeciesRecord;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class WormsService {

    static private Pattern REMOVE_TRAILING_JUNK_PATTERN = Pattern.compile("(( (sp\\.|spp\\.).+)|( \\(.+)|( \\[.+))",
            Pattern.CASE_INSENSITIVE);

    private final WebClient wormsClient;

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    public WormsService(@Qualifier("wormsClient") WebClient wormsClient) {
        this.wormsClient = wormsClient;
    }

    public List<SpeciesRecord> partialSearch(int page, String searchTerm) {

        try {
            Mono<SpeciesRecord[]> response = wormsClient
                    .get().uri(uriBuilder -> uriBuilder.path("/AphiaRecordsByName/" + removeTrailingJunk(searchTerm))
                            .queryParam("like", true)
                            .queryParam("marine_only", true)
                            .queryParam("offset", page * 50 + 1)
                            .build())
                    .retrieve()
                    .bodyToMono(SpeciesRecord[].class);
            SpeciesRecord[] matchingSpecies = Optional.ofNullable(response.block())
                    .orElse(new SpeciesRecord[0]);
            return Arrays.stream(matchingSpecies)
                    .map(m -> {
                        m.setIsPresent(StringUtils.isNotEmpty(m.getScientificName())
                                ? observableItemRepository.count(Example.of(
                                        ObservableItem.builder().observableItemName(m.getScientificName()).build())) > 0
                                : false);
                        return m;
                    })
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            return Arrays.asList();
        }
    }

    static String removeTrailingJunk(String searchTerm) {
        return REMOVE_TRAILING_JUNK_PATTERN.matcher(searchTerm).replaceAll("");
    }
}
