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

    @Schema(title = "Site Code")
    String getSiteCode();

    @Schema(title = "Has PQs")
    String getHasPQs();
    
    @Schema(title = "MPA")
    String getMPA();

    @Schema(title = "Country")
    String getCountry();

    @Schema(title = "Diver")
    String getDiverName();

    @Schema(title = "Location")
    String getLocationName();
}
