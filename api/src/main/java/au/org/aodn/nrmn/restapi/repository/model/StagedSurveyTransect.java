package au.org.aodn.nrmn.restapi.repository.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class StagedSurveyTransect {
    private String siteCode;
    private String date;
    private String depth;
    private String surveyNum;

    public StagedSurveyTransect(String siteCode, String date, String depth) {
        this.siteCode = siteCode;
        this.date = date;
        this.depth = StringUtils.substringBefore(depth, ".");
        this.surveyNum = StringUtils.substringAfter(depth, ".");
    }
}
