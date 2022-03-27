package au.org.aodn.nrmn.restapi.dto.observableitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@NoArgsConstructor
public class ObservableItemDto {
    @Id
    private Integer observableItemId;
    private String observableItemName;
    private Integer obsItemTypeId;
    private Integer aphiaId;
    private String commonName;
    private String phylum;
    @JsonProperty("class")
    private String className;
    private String order;
    private String family;
    private String genus;
    private String speciesEpithet;
    private String letterCode;
    private String reportGroup;
    private String habitatGroups;
    private String supersededBy;
}
