package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.Immutable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Immutable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ep_observable_items")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObservableItemListItem {

    @Id
    @Column(name = "observable_item_id")
    @Schema(title = "ID")
    private Integer id;

    @Column(name = "obs_item_type_name")
    @Schema(title = "Type")
    private String obsItemTypeName;
    
    @Column(name = "observable_item_name")
    @Schema(title = "Name")
    private String name;

    @Column(name = "common_name")
    @Schema(title = "Common Name")
    private String commonName;

    @Column(name = "superseded_by")
    @Schema(title = "Superseded By")
    private String supersededBy;

    @Column(name = "superseded_names")
    @Schema(title = "Superseded Names")
    private String supersededNames;

    @Column(name = "superseded_ids")
    @Schema(title = "Superseded IDs")
    private String supersededIDs;

    @Column(name = "phylum")
    @Schema(title = "Phylum")
    private String phylum;

    @Column(name = "class")
    @Schema(title = "Class")
    @JsonProperty(value = "class")
    private String classString;

    @Column(name = "order")
    @Schema(title = "Order")
    private String order;

    @Column(name = "family")
    @Schema(title = "Family")
    private String family;

    @Column(name = "genus")
    @Schema(title = "Genus")
    private String genus;
}
