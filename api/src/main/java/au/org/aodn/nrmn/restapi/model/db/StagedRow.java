package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "staged_row")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class StagedRow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="staged_row_id_seq")
    @SequenceGenerator(name="staged_row_id_seq", sequenceName = "staged_row_id_seq", allocationSize = 100)
    private Long id;

    @Column(name = "site_no")
    private String siteCode;

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

    @Column(name = "common_name")
    private String commonName;

    @Column(name = "total")
    private String total;

    @Column(name = "inverts")
    private String inverts;

    @Column(name = "position")
    private Integer pos;

    @Column(name = "is_invert_Sizing")
    private String isInvertSizing;

    @Column(name = "measure_value", columnDefinition = "json")
    @Type(type = "jsonb")
    private Map<Integer, String> measureJson;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "staged_row_staged_job_id_fkey"))
    @ToString.Exclude
    private StagedJob stagedJob;

    @Column(name = "created", columnDefinition = "timestamp with time zone", nullable = false)
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private Timestamp created;

    @Column(name = "last_updated", columnDefinition = "timestamp with time zone", nullable = false)
    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private Timestamp lastUpdated;

    @Transient
    @Builder.Default
    private List<StagedRowError> errors = new ArrayList<>();
}
