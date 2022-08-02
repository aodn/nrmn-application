package au.org.aodn.nrmn.restapi.dto.correction;

public interface CorrectionRowDto {
    String getObservationIds();

    Integer getSurveyId();
    
    Integer getSurveyNum();

    Integer getDiverId();

    String getDiver();

    String getSiteCode();

    String getDirection();

    String getLatitude();

    String getLongitude();

    String getIsInvertSizing();

    String getObservableItemId();

    String getSpecies();

    String getLetterCode();

    String getMethod();

    String getBlock();

    Integer getDepth();

    String getDate();

    String getTime();

    Integer getVis();

    String getMeasureJson();
}
