package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

@Entity
@Data
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "staged_row")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class StagedRow implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "Site No")
    @Column(name = "site_no")
    private String siteNo;

    @Column(name = "date")
    private String date;

    @Column(name = "diver")
    private String diver;

    @Column(name = "depth")
    private String depth;

    @Column(name = "method")
    private String method;

    @Column(name = "block")
    private String block;

    @Column(name = "species")
    private String species;

    @Column(name = "buddy")
    private String buddy;

    @JsonProperty(value = "Site Name")
    @Column(name = "site_name")
    private String siteName;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "vis")
    private String vis;

    @Column(name = "time")
    private String time;

    @Column(name = "direction")
    private String direction;

    @JsonProperty(value = "P-Qs")
    @Column(name = "PQs")
    private String pqs;

    @Column(name = "code")
    private String code;

    @JsonProperty(value = "Common name")
    @Column(name = "common_name")
    private String commonName;

    @Column(name = "total")
    private String total;

    @Column(name = "inverts")
    private String inverts;

    @Column(name = "m2_invert_sizing_species")
    private String m2InvertSizingSpecies;

    @Column(name = "L5")
    private String l5;

    @Column(name = "L95")
    private String l95;

    @Column(name = "is_invert_Sizing")
    private String isInvertSizing;

    @Column(name = "Lmax")
    private String lmax;

    @Column(name = "measure_value", columnDefinition = "json")
    @Type(type = "jsonb")
    private Map<String, String> measureJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "staged_row_staged_job_id_fkey"))
    private StagedJob stagedJob;

    @Column(name = "created", columnDefinition = "timestamp with time zone", nullable = false)
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private Timestamp created;

    @Column(name = "last_updated", columnDefinition = "timestamp with time zone", nullable = false)
    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private Timestamp lastUpdated;

}