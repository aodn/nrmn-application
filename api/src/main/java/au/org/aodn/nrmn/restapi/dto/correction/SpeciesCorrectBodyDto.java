package au.org.aodn.nrmn.restapi.dto.correction;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpeciesCorrectBodyDto implements Serializable {
    private SpeciesSearchBodyDto filterSet;
    private Integer prevObservableItemId;
    private Integer newObservableItemId;
    private List<Integer> surveyIds;
}
