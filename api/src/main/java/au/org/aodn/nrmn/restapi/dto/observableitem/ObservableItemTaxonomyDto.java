package au.org.aodn.nrmn.restapi.dto.observableitem;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObservableItemTaxonomyDto {
    private List<String> phylum;
    private List<String> className;
    private List<String> order;
    private List<String> family;
    private List<String> genus;
    private List<String> speciesEpithet;
}
