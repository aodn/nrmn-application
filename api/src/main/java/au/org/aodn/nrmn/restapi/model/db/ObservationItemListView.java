package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * This view is use by the GUI observation screen, we do not use the Observation db entity directly because
 * we want to allow flexible table join here if needed in future
 */
@Entity
@Immutable
@Audited(withModifiedFlag = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Subselect(
        "SELECT " +
                "oi.observable_item_id as observableitemid, oitr.obs_item_type_name as typename, oi.observable_item_name as name, " +
                "oi.common_name as commonname, oi.phylum, oi.class as classname, oi.\"order\", oi.family, oi.genus, oi.superseded_by as supersededby, " +
                "superseded.supersedednames, superseded.supersededids " +
                "FROM nrmn.observable_item_ref oi " +
                "LEFT JOIN nrmn.obs_item_type_ref oitr ON oitr.obs_item_type_id = oi.obs_item_type_id " +
                "LEFT JOIN LATERAL (" +
                    "SELECT " +
                        "string_agg(oi_1.observable_item_name, ', ' ORDER BY oi_1.observable_item_name) as supersededNames, " +
                        "string_agg(cast(oi_1.observable_item_id AS varchar ), ', ' ORDER BY oi_1.observable_item_name) as supersededIds " +
                        "FROM nrmn.observable_item_ref oi_1 " +
                        "WHERE oi_1.superseded_by = oi.observable_item_name) AS superseded ON true"
)
public class ObservationItemListView {

    @Id
    @Column(name = "observableitemid")
    @Audited(targetAuditMode = NOT_AUDITED)
    private Integer observableItemId;

    @Column(name = "typename")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String typeName;

    @Column(name = "name")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String name;

    @Column(name = "commonname")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String commonName;

    @Column(name = "phylum")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String phylum;

    @Column(name = "classname")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String className;

    @Column(name = "order")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String order;

    @Column(name = "family")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String family;

    @Column(name = "genus")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String genus;

    @Column(name = "supersededby")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String supersededBy;

    @Column(name = "supersedednames")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String supersededNames;

    @Column(name = "supersededids")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String supersededIds;

    @JsonGetter
    public Integer getObservableItemId() {
        return observableItemId;
    }

    @JsonGetter
    public String getTypeName() {
        return typeName;
    }

    @JsonGetter
    public String getName() {
        return name;
    }

    @JsonGetter
    public String getCommonName() {
        return commonName;
    }

    @JsonGetter
    public String getPhylum() {
        return phylum;
    }

    @JsonGetter("class")
    public String getClassName() {
        return className;
    }

    @JsonGetter
    public String getOrder() {
        return order;
    }

    @JsonGetter
    public String getFamily() {
        return family;
    }

    @JsonGetter
    public String getGenus() {
        return genus;
    }

    @JsonGetter
    public String getSupersededBy() {
        return supersededBy;
    }

    @JsonGetter
    public String getSupersededNames() {
        return supersededNames;
    }

    @JsonGetter("supersededIDs")
    public String getSupersededIds() {
        return supersededIds;
    }
}
