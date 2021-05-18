package au.org.aodn.nrmn.restapi.dto.observableitem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
public class ObservableItemPutDto {

    @Id
    @Schema(hidden = true)
    private Integer observableItemId;

    @Id
    @Schema(hidden = true)
    private Integer obsItemTypeId;

    @Schema(title = "Species Name", accessMode = Schema.AccessMode.READ_ONLY)
    private String observableItemName;

    @Schema(title = "Common Name")
    private String commonName;

    @Schema(title = "Phylum", accessMode = Schema.AccessMode.READ_ONLY)
    private String phylum;

    @Schema(title = "Class", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("class")
    private String className;

    @Schema(title = "Order", accessMode = Schema.AccessMode.READ_ONLY)
    private String order;

    @Schema(title = "Family", accessMode = Schema.AccessMode.READ_ONLY)
    private String family;

    @Schema(title = "Genus", accessMode = Schema.AccessMode.READ_ONLY)
    private String genus;

    @Schema(title = "Species Epithet", accessMode = Schema.AccessMode.READ_ONLY)
    private String speciesEpithet;

    @Schema(title = "Superseded By")
    private String supersededBy;

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
}
