package au.org.aodn.nrmn.restapi.repository.projections;

public interface SurveyRow {
    Integer getSurveyId();

    String getSiteName();

    String getProgramName();

    String getSurveyDate();

    String getSurveyTime();

    String getDepth();

    String getSurveyNum();

    String getSiteCode();

    String getHasPQs();

    String getMPA();

    String getCountry();

    String getDiverName();

    String getLocationName();
}
