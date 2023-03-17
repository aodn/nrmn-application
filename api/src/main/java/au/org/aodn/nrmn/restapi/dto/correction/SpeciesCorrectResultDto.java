package au.org.aodn.nrmn.restapi.dto.correction;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpeciesCorrectResultDto {
    private Long jobId;
    private String message;
    private String currentSpeciesName;
    private String nextSpeciesName;
    private Integer[] surveyIds;
}
