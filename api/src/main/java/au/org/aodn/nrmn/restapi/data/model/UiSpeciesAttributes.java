package au.org.aodn.nrmn.restapi.data.model;

public interface UiSpeciesAttributes {
     Long getId();

     String getSpeciesName();

     String getCommonName();

     Boolean getIsInvertSized();

     Double getL5();

     Double getL95();

     Long getMaxAbundance();

     Double getLmax();

}
