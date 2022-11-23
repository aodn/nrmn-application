package au.org.aodn.nrmn.restapi.dto.species;

public interface SpeciesCorrectDto {
    Integer getObservableItemId();
    String getCommonName();
    String getObservableItemName();
    String getSupersededBy();
    String getSurveyJson();
}
