package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "observable_item_ref")
@SecondaryTable(name = "lengthweight_ref", pkJoinColumns = @PrimaryKeyJoinColumn(name = "observable_item_id"),
 foreignKey = @ForeignKey(name = "lengthweight_ref_observable_item_id_fkey"))
@Audited(withModifiedFlag = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObservableItem {
    @Id
    @SequenceGenerator(name = "observable_item_ref_observable_item_id", sequenceName =
            "observable_item_ref_observable_item_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "observable_item_ref_observable_item_id")
    @Column(name = "observable_item_id", unique = true, updatable = false, nullable = false)
    @Schema(title = "Id", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer observableItemId;

    @Basic
    @Column(name = "observable_item_name")
    @NotNull
    @Schema(title = "Name")
    private String observableItemName;

    @Basic
    @Column(name = "common_name")
    @Schema(title = "Common name")
    private String commonName;

    @Basic
    @Column(name = "superseded_by")
    @Schema(title = "Superseded by")
    private String supersededBy;

    @Basic
    @Column(name = "phylum")
    @Schema(title = "Phylum")
    private String phylum;

    @Basic
    @Column(name = "class")
    @Audited(withModifiedFlag = true, modifiedColumnName = "class_mod")
    @Schema(title = "Class")
    private String clazz;

    @Basic
    @Column(name = "\"order\"")
    @Schema(title = "Order")
    private String order;

    @Basic
    @Column(name = "family")
    @Schema(title = "Family")
    private String family;

    @Basic
    @Column(name = "genus")
    @Schema(title = "Genus")
    private String genus;

    @Basic
    @Column(name = "report_group")
    @Schema(title = "Report group")
    private String reportGroup;

    @Basic
    @Column(name = "habitat_groups")
    @Schema(title = "Habitat groups")
    private String habitatGroups;

    @Basic
    @Column(name = "letter_code")
    @Schema(title = "Letter code")
    private String letterCode;

    @Basic
    @Column(name = "obs_item_attribute", columnDefinition = "jsonb")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Type(type = "jsonb")
    private Map<String, String> obsItemAttribute;

    @Embedded
    @Audited(targetAuditMode = NOT_AUDITED)
    @Valid
    @Schema(title = "Length weight")
    private LengthWeight lengthWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obs_item_type_id", referencedColumnName = "obs_item_type_id", nullable = false)
    @Schema(title = "Type", implementation = String.class, format = "uri")
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    @NotNull
    private ObsItemType obsItemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aphia_id", referencedColumnName = "aphia_id")
    @Schema(title = "Aphia id", implementation = String.class, format = "uri")
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    private AphiaRef aphiaRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aphia_rel_type_id", referencedColumnName = "aphia_rel_type_id")
    @Schema(title = "Aphia relation type", implementation = String.class, format = "uri")
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    private AphiaRelType aphiaRelType;
}
