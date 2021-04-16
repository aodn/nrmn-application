package au.org.aodn.nrmn.restapi.dto.survey;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Stream;

@Data
public class SurveyFilterDto {

    public SurveyFilterDto(String startDate, String endDate) {
        this.filterSet = Stream.of(startDate, endDate).noneMatch(Objects::isNull);
        this.startDateTimestamp = startDate != null ? Timestamp.from(Instant.parse(startDate)) : Timestamp.from(Instant.ofEpochSecond(0));
        this.endDateTimestamp = endDate != null ? Timestamp.from(Instant.parse(endDate)) : Timestamp.from(Instant.now());
    }
    
    public Boolean isSet() { return filterSet; }

    private Boolean filterSet;
    private Timestamp startDateTimestamp;
    private Timestamp endDateTimestamp;

    private Integer surveyId;
    private Integer diverId;
    private Integer programId;
    private Integer methodId;
    private Integer speciesId;
    private Integer locationId;
    private String siteId;
    private String country;
    private String state;
    private String siteCode;
    private String ecoRegion;
    private Integer depth;
    private Double latitude;
    private Double longitude;
}
