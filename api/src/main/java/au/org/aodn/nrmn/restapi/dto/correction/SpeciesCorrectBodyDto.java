package au.org.aodn.nrmn.restapi.dto.correction;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpeciesCorrectBodyDto {
    private Integer prevObservableItemId;
    private Integer newObservableItemId;
    private List<Integer> surveyIds;
}
