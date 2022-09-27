package au.org.aodn.nrmn.db.model;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "observable_item_ref")
@SecondaryTable(name = "lengthweight_ref", pkJoinColumns = @PrimaryKeyJoinColumn(name = "observable_item_id"),
 foreignKey = @ForeignKey(name = "lengthweight_ref_observable_item_id_fkey"))
@Audited(withModifiedFlag = true)
public class ObservableItem {
    @Id
    @SequenceGenerator(name = "observable_item_ref_observable_item_id", sequenceName =
            "observable_item_ref_observable_item_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "observable_item_ref_observable_item_id")
    @Column(name = "observable_item_id", unique = true, updatable = false, nullable = false)
    private Integer observableItemId;
    
    @NotAudited
    @CreationTimestamp 
    @Column(name = "created", updatable = false)
    private LocalDateTime created;

    @NotAudited
    @UpdateTimestamp
    @Column(name = "updated")
    private LocalDateTime updated;

    @Basic
    @Column(name = "observable_item_name")
    @NotNull(message = "Species Name is required.")
    private String observableItemName;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Observable Item Type is required.")
    @JoinColumn(name = "obs_item_type_id", referencedColumnName = "obs_item_type_id", nullable = false)
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    private ObsItemType obsItemType;
    
    @Basic
    @Column(name = "common_name")
    private String commonName;

    @Basic
    @Column(name = "phylum")
    private String phylum;

    @Basic
    @Column(name = "class")
    @Audited(withModifiedFlag = true, modifiedColumnName = "class_mod")
    private String className;

    @Basic
    @Column(name = "\"order\"")
    private String order;

    @Basic
    @Column(name = "family")
    private String family;

    @Basic
    @Column(name = "genus")
    private String genus;

    @Basic
    @Column(name = "species_epithet")
    private String speciesEpithet;

    @Basic
    @Column(name = "letter_code")
    private String letterCode;

    @Basic
    @Column(name = "report_group")
    private String reportGroup;

    @Basic
    @Column(name = "habitat_groups")
    private String habitatGroups;

    @Basic
    @Column(name = "superseded_by")
    private String supersededBy;
    
    @Basic
    @Column(name = "is_invert_sized")
    private Boolean isInvertSized;

    @Basic
    @Column(name = "obs_item_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, String> obsItemAttribute;

    @Embedded
    @Audited(targetAuditMode = NOT_AUDITED)
    @Valid
    private LengthWeight lengthWeight;

    @Basic
    @Column(name = "aphia_id")
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    private Integer aphiaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aphia_rel_type_id", referencedColumnName = "aphia_rel_type_id")
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    private AphiaRelType aphiaRelType;

    @OneToMany()
    @JoinTable(name = "methods_species", joinColumns = @JoinColumn(name = "observable_item_id"),
      inverseJoinColumns = @JoinColumn(name = "method_id"))
    @NotAudited
    @EqualsAndHashCode.Exclude
    private Set<Method> methods;
}
