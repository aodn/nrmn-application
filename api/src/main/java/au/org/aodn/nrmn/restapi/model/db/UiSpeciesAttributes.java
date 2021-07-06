package au.org.aodn.nrmn.restapi.model.db;

public interface UiSpeciesAttributes {
     Long getId();

     String getSpeciesName();

     String getCommonName();

     Boolean getIsInvertSized();

     Double getL5();

     Double getL95();

     Long getMaxAbundance();

     Long getLmax();

}
