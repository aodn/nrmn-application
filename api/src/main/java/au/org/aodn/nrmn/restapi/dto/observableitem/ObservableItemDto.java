package au.org.aodn.nrmn.restapi.dto.observableitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ObservableItemDto {

    @Id
    @Schema(title = "ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer observableItemId;

    @Schema(title = "Species Name")
    private String observableItemName;

    @Schema(title = "Observable Item Type")
    @NotNull(message = "Observable Item Type is required")
    private Integer obsItemTypeId;

    @Schema(title = "Aphia ID")
    private Integer aphiaId;
    
    @Schema(title = "Common Name")
    private String commonName;

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

    @Schema(title = "Species Epithet")
    @NotNull(message =  "Species Epithet is required.")
    private String speciesEpithet;

    @Schema(title = "Letter Code")
    private String letterCode;

    @Schema(title = "Report Group")
    private String reportGroup;

    @Schema(title = "Habitat Groups")
    private String habitatGroups;

    @Schema(title = "Superseded By")
    private String supersededBy;
}
