package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Map;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "observable_item_ref")
@Audited(withModifiedFlag = true)
public class ObservableItem {
    @Id
    @SequenceGenerator(name = "observable_item_ref_observable_item_id", sequenceName = "observable_item_ref_observable_item_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "observable_item_id", unique = true, updatable = false, nullable = false)
    private int observableItemId;

    @Basic
    @Column(name = "observable_item_name")
    private String observableItemName;

    @Basic
    @Column(name = "obs_item_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, String> obsItemAttribute;

    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "observableItem", cascade = CascadeType.ALL)
    private LengthWeight lengthWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obs_item_type_id", referencedColumnName = "obs_item_type_id", nullable = false)
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    @JsonIgnore
    private ObsItemType obsItemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aphia_id", referencedColumnName = "aphia_id")
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    @JsonIgnore
    private AphiaRef aphiaRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aphia_rel_type_id", referencedColumnName = "aphia_rel_type_id")
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    @JsonIgnore
    private AphiaRelType aphiaRelType;

    public void setLengthWeight(LengthWeight lengthWeight) {
        // set lengthWeight ensuring backreferences are also updated as required
        if (this.lengthWeight != null) this.lengthWeight.setObservableItem(null);
        this.lengthWeight = lengthWeight;
        if (this.lengthWeight != null) this.lengthWeight.setObservableItem(this);
    }

}
