package au.org.aodn.nrmn.restapi.dto.correction;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface CorrectionRowDto {
    String getObservationIds();

    Integer getSurveyId();

    String getDiver();

    @JsonProperty(value = "P-Qs")
    String getPqDiver();

    String getSiteCode();

    String getSiteName();

    String getDirection();

    String getLatitude();

    String getLongitude();

    String getIsInvertSizing();

    String getSpecies();

    String getCommonName();

    String getCode();

    String getMethod();

    String getBlock();

    String getDepth();

    String getDate();

    String getTime();

    String getVis();

    String getMeasureJson();
}
