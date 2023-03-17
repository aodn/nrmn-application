package au.org.aodn.nrmn.restapi.dto.survey;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SurveyListDto {
    private List<SurveyDto> items;
    private Long lastRow;
}
