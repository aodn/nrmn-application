package au.org.aodn.nrmn.restapi.data.model;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.hibernate.annotations.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @Valid
    @NotAudited
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinFormula(value="(select case " +
            "when superseded_by = '' or superseded_by is NULL then observable_item_id " +
            "else (select r.observable_item_id from {h-schema}observable_item_ref r where r.observable_item_name = superseded_by) " +
            "end) ", referencedColumnName = "observable_item_id")
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
