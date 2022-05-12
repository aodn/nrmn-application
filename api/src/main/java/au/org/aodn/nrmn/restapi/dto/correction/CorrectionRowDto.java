package au.org.aodn.nrmn.restapi.dto.correction;

public interface CorrectionRowDto {
    String getObservationIds();

    Integer getSurveyId();

    Integer getDiverId();

    String getInitials();

    String getSiteCode();

    String getDirection();

    String getLatitude();

    String getLongitude();

    Boolean getUseInvertSizing();

    String getObservableItemId();

    String getObservableItemName();

    String getLetterCode();

    String getMethodId();

    String getBlockNum();

    Integer getDepth();

    String getSurveyDate();

    String getSurveyTime();

    Integer getVisibility();

    String getMeasurementJson();
}
