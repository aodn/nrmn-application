package au.org.aodn.nrmn.restapi.dto.correction;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpeciesSearchBodyDto implements Serializable {
    private Integer observableItemId;
    private String startDate;
    private String endDate;
    private String geometry;
    private Integer offset;
    private List<Integer> locationIds;
}
