package au.org.aodn.nrmn.restapi.repository.projections;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpeciesWithAttributesCsvRow {

    private String letterCode;

    private String speciesName;

    private String commonName;

    private Boolean isInvertSized;

    private Double l5;

    private Double l95;

    private Integer lMax;
}
