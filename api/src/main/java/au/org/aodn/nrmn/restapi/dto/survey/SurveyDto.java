package au.org.aodn.nrmn.restapi.dto.survey;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.Id;

@Data
@NoArgsConstructor
public class SurveyDto {
    @Id
    private Integer surveyId;
    private String siteName;
    private String siteCode;
    private String program;
    private Integer programId;
    private String surveyDate;
    private String surveyTime;
    private String depth;
    private Integer surveyNum;
    private String visibility;
    private String direction;
    private String method;
    private String block;
    private String longitude;
    private String latitude;
    private String siteLongitude;
    private String siteLatitude;
    private String protectionStatus;
    private String insideMarinePark;
    private String notes;
    private String pqCatalogued;
    private String pqZipUrl;
    private String pqDiver;
    private String pqDiverInitials;
    private Boolean blockAbundanceSimulated;
    private String projectTitle;
    private String surveyNotDone;
    private String locationName;
    private String area;
    private String country;
    private String divers;
    private String decimalDepth;
    private Date created;
    private Date updated;
}
