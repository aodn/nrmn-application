package au.org.aodn.nrmn.restapi.dto.correction;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface CorrectionRowDto {
    String getObservationIds();

    Integer getSurveyId();
    
    Integer getSurveyNum();

    Integer getDiverId();

    String getDiver();

    @JsonProperty(value = "P-Qs")
    String getPqDiver();

    String getSiteCode();

    String getDirection();

    String getLatitude();

    String getLongitude();

    String getIsInvertSizing();

    String getObservableItemId();

    String getSpecies();

    String getCode();

    String getMethod();

    String getBlock();

    Integer getDepth();

    String getDate();

    String getTime();

    Integer getVis();

    String getMeasureJson();
}
