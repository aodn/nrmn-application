package au.org.aodn.nrmn.restapi.dto.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@NoArgsConstructor
public class SurveyDto {
    @Id
    @Schema(title = "Survey ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer surveyId;

    @Schema(title = "Site Name", accessMode = Schema.AccessMode.READ_ONLY)
    private String siteName;

    @Schema(title = "Site Code")
    private String siteCode;
    
    @Schema(title = "Program")
    private String program;

    @Schema(title = "Program ID")
    private Integer programId;

    @Schema(title = "Survey Date")
    private String surveyDate;

    @Schema(title = "Survey Time")
    private String surveyTime;

    @Schema(title = "Depth")
    private String depth;

    @Schema(title = "Survey Number")
    private Integer surveyNum;

    @Schema(title = "Visibility")
    private String visibility;

    @Schema(title = "Direction")
    private String direction;

    @Schema(title = "Method(s)")
    private String method;

    @Schema(title = "Block")
    private String block;

    @Schema(title = "Survey Longitude")
    private String longitude;

    @Schema(title = "Survey Latitude")
    private String latitude;

    @Schema(title = "Survey Protection Status")
    private String protectionStatus;

    @Schema(title = "Inside Marine Park")
    private String insideMarinePark;

    @Schema(title = "Notes")
    private String notes;

    @Schema(title = "PQ Catalogued")
    private String pqCatalogued;

    @Schema(title = "PQ Zip Url")
    private String pqZipUrl;

    @Schema(title = "PQ diver")
    private String pqDiver;

    @Schema(title = "PQ Diver")
    private String pqDiverInitials;

    @Schema(title = "Block Abundance Simulated")
    private Boolean blockAbundanceSimulated;

    @Schema(title = "Project Title")
    private String projectTitle;

    @Schema(title = "Survey(s) Not Done")
    private String surveyNotDone;

    @Schema(title = "Location Name")
    private String locationName;

    @Schema(title = "Area")
    private String area;

    @Schema(title = "Country")
    private String country;

    @Schema(title = "Divers")
    private String divers;
}
