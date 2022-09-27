package au.org.aodn.nrmn.db.model;

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
