package au.org.aodn.nrmn.restapi.dto.correction;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpeciesSearchBodyDto {
    private Integer observableItemId;
    private String startDate;
    private String endDate;
    private String geometry;
    private List<Integer> locationIds;
}
