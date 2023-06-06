package au.org.aodn.nrmn.restapi.dto.observableitem;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
public class ObservableItemGetDto {

    @Id
    private Integer observableItemId;
    private String observableItemName;
    private String speciesEpithet;
    private String obsItemTypeName;
    private Integer obsItemTypeId;
    private String commonName;
    private String aphiaId;
    private String aphiaRelTypeName;
    private String supersededBy;

    // List of species that supersed this species
    private String supersededNames;
    private String supersededIds;

    private String phylum;
    @JsonProperty("class")
    private String className;
    private String order;
    private String family;
    private String genus;
    private String letterCode;
    private String reportGroup;
    private String habitatGroups;
    private Boolean isInvertSized;
    private Double lengthWeightA;
    private Double lengthWeightB;
    private Double lengthWeightCf;
    private Map<String, String> obsItemAttribute;
}
