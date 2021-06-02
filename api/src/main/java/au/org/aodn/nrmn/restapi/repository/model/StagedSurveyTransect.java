package au.org.aodn.nrmn.restapi.repository.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class StagedSurveyTransect {
    private String siteCode;
    private String date;
    private String depth;
    private String surveyNum;
    private String method;
    private String block;

    public StagedSurveyTransect(String siteCode, String date, String depth, String method, String block) {
        this.siteCode = siteCode;
        this.date = date;
        this.depth = StringUtils.substringBefore(depth, ".");
        this.surveyNum = StringUtils.substringAfter(depth, ".");
        this.method = method;
        this.block = block;
    }
}
