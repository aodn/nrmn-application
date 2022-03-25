package au.org.aodn.nrmn.restapi.dto.observableitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@NoArgsConstructor
public class ObservableItemPutDto {

    @Id
    private Integer observableItemId;

    @Id
    private Integer obsItemTypeId;
    private String observableItemName;
    private String commonName;
    private String phylum;
    @JsonProperty("class")
    private String className;
    private String order;
    private String family;
    private String genus;
    private String speciesEpithet;
    private String supersededBy;
    private String letterCode;
    private String reportGroup;
    private String habitatGroups;
    private Double lengthWeightA;
    private Double lengthWeightB;
    private Double lengthWeightCf;
}
