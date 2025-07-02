package au.org.aodn.nrmn.restapi.dto.correction;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SpeciesCorrectResultDto implements Serializable {
    private Long jobId;
    private String message;
    private String currentSpeciesName;
    private String nextSpeciesName;
    private Integer[] surveyIds;
}
