package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.db.model.ObservableItem;
import au.org.aodn.nrmn.db.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.dto.species.SpeciesRecordDto;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class WormsService {

    static private Pattern REMOVE_TRAILING_JUNK_PATTERN = Pattern.compile("(( (sp\\.|spp\\.).+)|( \\(.+)|( \\[.+))",
            Pattern.CASE_INSENSITIVE);

    private final WebClient wormsClient;

    protected final int API_DEFAULT_BATCH_SIZE = 50;

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    public WormsService(@Qualifier("wormsClient") WebClient wormsClient) {
        this.wormsClient = wormsClient;
    }

    public List<SpeciesRecordDto> partialSearch(final int page, final int pageSize, final String searchTerm) {

        try {
            List<SpeciesRecordDto> records = new ArrayList<>();
            Boolean done = Boolean.FALSE;
            int itemRemain = pageSize;
            final AtomicInteger p = new AtomicInteger(page);
            final AtomicInteger pz = new AtomicInteger(pageSize);

            while(!done) {
                Mono<SpeciesRecordDto[]> response = wormsClient
                        .get().uri(uriBuilder -> uriBuilder.path("/AphiaRecordsByName/" + removeTrailingJunk(searchTerm))
                                .queryParam("like", true)
                                .queryParam("marine_only", true)
                                .queryParam("offset", p.get() * pz.get() + 1)
                                .build())
                        .retrieve()
                        .bodyToMono(SpeciesRecordDto[].class);

                SpeciesRecordDto[] matchingSpecies = Optional.ofNullable(response.block()).orElse(new SpeciesRecordDto[0]);

                List<SpeciesRecordDto> i = Arrays.stream(matchingSpecies)
                        .map(m -> {
                            m.setIsPresent(StringUtils.isNotEmpty(m.getScientificName())
                                    ? observableItemRepository.count(Example.of(
                                    ObservableItem.builder().observableItemName(m.getScientificName()).build())) > 0
                                    : false);
                            return m;
                        })
                        .collect(Collectors.toList());

                // The api batch size is always 50, if item return is 50 and your item remain > 0
                // then you need to get the rest of the items, otherwise you can return
                if(i.size() == API_DEFAULT_BATCH_SIZE && itemRemain - i.size() > 0) {
                    records.addAll(i);
                    itemRemain -= i.size();
                    p.incrementAndGet();

                    // The pageSize is only useful for initial offset, since the api server
                    // page size is always 50, after calculate the initial offset, the rest
                    // of the page will back to 50
                    pz.set(50);
                }
                else {
                    // Since batch is always 50, the total number may exceed user want
                    records.addAll(i.subList(0, i.size() < itemRemain ? i.size() : itemRemain));
                    done = Boolean.TRUE;
                }
            }

            return records;
        }
        catch (Exception ex) {
            return Arrays.asList();
        }
    }

    static String removeTrailingJunk(String searchTerm) {
        return REMOVE_TRAILING_JUNK_PATTERN.matcher(searchTerm).replaceAll("");
    }
}
