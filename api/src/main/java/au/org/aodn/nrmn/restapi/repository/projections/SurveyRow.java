package au.org.aodn.nrmn.restapi.repository.projections;

import io.swagger.v3.oas.annotations.media.Schema;

public interface SurveyRow {

    @Schema(title = "ID")
    Integer getSurveyId();

    @Schema(title = "Site Name")
    String getSiteName();
    
    @Schema(title = "Program")
    String getProgramName();

    @Schema(title = "Survey Date")
    String getSurveyDate();

    @Schema(title = "Survey Time")
    String getSurveyTime();

    @Schema(title = "Depth")
    String getDepth();

    @Schema(title = "Survey Number")
    String getSurveyNum();
}
