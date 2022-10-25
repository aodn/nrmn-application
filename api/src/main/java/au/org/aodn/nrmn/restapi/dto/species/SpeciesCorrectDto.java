package au.org.aodn.nrmn.restapi.dto.species;

public interface SpeciesCorrectDto {
    Integer getSurveyId();
    String getSurveyDate();
    Integer getObservableItemId();
    String getObservableItemName();
    String getCommonName();
    String getLocationName();
}
