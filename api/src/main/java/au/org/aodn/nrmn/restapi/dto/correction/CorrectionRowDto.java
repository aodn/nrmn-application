package au.org.aodn.nrmn.restapi.dto.correction;

public interface CorrectionRowDto {
    String getObservationIds();

    Integer getSurveyId();

    Integer getDiverId();

    String getInitials();

    String getSiteCode();

    Integer getDepth();

    String getSurveyDate();

    String getSurveyTime();

    Integer getVisibility();

    String getMeasurementJson();
}
