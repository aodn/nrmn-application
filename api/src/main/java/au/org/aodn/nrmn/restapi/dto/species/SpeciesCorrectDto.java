package au.org.aodn.nrmn.restapi.dto.species;

public interface SpeciesCorrectDto {
    Integer getObservableItemId();
    String getCommonName();
    String getObservableItemName();
    String getSupersededBy();
    Integer getSurveyCount();
    
    String getSurveyIds();
    String getLocationName();
    String getSiteName();
}
