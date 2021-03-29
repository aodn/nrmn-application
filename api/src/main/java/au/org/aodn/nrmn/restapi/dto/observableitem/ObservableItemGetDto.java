package au.org.aodn.nrmn.restapi.dto.observableitem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
public class ObservableItemGetDto {

    @Id
    @Schema(title = "ID")
    private Integer observableItemId;

    @Schema(title = "Species Name")
    private String observableItemName;

    @Schema(title = "Observable Item Type")
    private String obsItemTypeName;

    @Schema(hidden = true)
    private Integer obsItemTypeId;

    @Schema(title = "Common Name")
    private String commonName;

    @Schema(title = "Aphia ID")
    private String aphiaId;

    @Schema(title = "Aphia Relation")
    private String aphiaRelTypeName;

    @Schema(title = "Superseded By")
    private String supersededBy;

    @Schema(title = "Superseded By Names")
    private String supersededByNames;

    @Schema(title = "Superseded By IDs")
    private String supersededByIDs;

    @Schema(title = "Phylum")
    private String phylum;

    @Schema(title = "Class")
    @JsonProperty("class")
    private String className;

    @Schema(title = "Order")
    private String order;

    @Schema(title = "Family")
    private String family;

    @Schema(title = "Genus")
    private String genus;

    @Schema(title = "Letter Code")
    private String letterCode;

    @Schema(title = "Report Group")
    private String reportGroup;

    @Schema(title = "Habitat Groups")
    private String habitatGroups;

    @Schema(title = "Length-Weight a")
    private Double lengthWeightA;

    @Schema(title = "Length-Weight b")
    private Double lengthWeightB;

    @Schema(title = "Length-Weight cf")
    private Double lengthWeightCf;

    @Schema(title = "Other Attributes")
    private String obsItemAttribute;
}
