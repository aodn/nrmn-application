package au.org.aodn.nrmn.restapi.dto.correction;

import java.util.Collection;
import java.util.Map;
import org.hibernate.annotations.Type;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CorrectionRowPutDto {
    Collection<Integer> observationIds;
    Long surveyId;
    Long diverId;
    String initials;
    String siteCode;
    Integer depth;
    String surveyDate;
    String surveyTime;
    Integer visibility;
    String direction;
    String latitude;
    String longitude;
    Integer observableItemId;
    String observableItemName;
    String letterCode;
    Integer methodId;
    Integer blockNum;
    Boolean surveyNotDone;
    Boolean useInvertSizing;
    @Type(type = "jsonb")
    Map<Integer, String> measurements;
}
