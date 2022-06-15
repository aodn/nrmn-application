package au.org.aodn.nrmn.restapi.dto.correction;

public interface CorrectionRowDto {
    String getObservationIds();

    Integer getSurveyId();

    Integer getDiverId();

    String getDiver();

    String getSiteCode();

    String getDirection();

    String getLatitude();

    String getLongitude();

    Boolean getIsInvertSizing();

    String getObservableItemId();

    String getSpecies();

    String getLetterCode();

    String getMethod();

    String getBlockNum();

    Integer getDepth();

    String getDate();

    String getTime();

    Boolean getSurveyNotDone();

    Integer getVis();

    String getMeasureJson();
}
