package au.org.aodn.nrmn.restapi.model.db;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import au.org.aodn.nrmn.restapi.util.TimeUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    @Builder.Default
    private String siteCode = "";

    @Column(name = "date")
    @Builder.Default
    private String date = "";

    @Column(name = "diver")
    @Builder.Default
    private String diver = "";

    @Column(name = "depth")
    @Builder.Default
    private String depth = "";

    @Column(name = "method")
    @Builder.Default
    private String method = "";

    @Column(name = "block")
    @Builder.Default
    private String block = "";

    @Column(name = "species")
    @Builder.Default
    private String species = "";

    @Column(name = "buddy")
    @Builder.Default
    private String buddy = "";

    @Column(name = "site_name")
    @Builder.Default
    private String siteName = "";

    @Column(name = "longitude")
    @Builder.Default
    private String longitude = "";

    @Column(name = "latitude")
    @Builder.Default
    private String latitude = "";

    @Column(name = "vis")
    @Builder.Default
    private String vis = "";

    @Column(name = "time")
    @Builder.Default
    private String time = "";

    @Column(name = "direction")
    @Builder.Default
    private String direction = "";

    @JsonProperty(value = "P-Qs")
    @Column(name = "PQs")
    @Builder.Default
    private String pqs = "";

    @Column(name = "code")
    @Builder.Default
    private String code = "";

    @Column(name = "common_name")
    @Builder.Default
    private String commonName = "";

    @Column(name = "total")
    @Builder.Default
    private String total = "";

    @Column(name = "inverts")
    @Builder.Default
    private String inverts = "";

    @Column(name = "position")
    private Integer pos;

    @Column(name = "is_invert_Sizing")
    @Builder.Default
    private String isInvertSizing = "";

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

    private String dateNormalised() {
        try {
            LocalDate d = LocalDate.parse(date, TimeUtils.getRowDateFormatter());
            return d != null ? d.toString() : date;
        } catch (Exception e) {
            return date;
        }
    }

    public String getContentsHash(boolean includeTotal) {
        // String measurements = measureJson.entrySet().stream().map(m -> m.getValue().length() > 0 ? m.getKey().toString() + ":" + m.getValue() + "|" : "").reduce("", (a, b) -> a + b);
        String rowContents = siteCode + dateNormalised() + diver + depth + method + block + species + buddy + siteName + longitude + latitude + vis + time + direction + pqs + code + commonName + inverts + isInvertSizing; // + total + measurements;
        if(includeTotal) rowContents += total;
        return Integer.toString(rowContents.hashCode());
    }

    public String getSurveyGroup() {
        String depthPart = depth.split("\\.")[0];
        return (siteCode + "/" + dateNormalised() + "/" + depthPart).toUpperCase();
    }

    public String getSurvey() {
        return String.format("[%s, %s, %s]", siteCode,  dateNormalised(), depthPart).toUpperCase();
    }

    public String getSurvey() {
        return String.format("[%s, %s, %s]", siteCode,  dateNormalised(), depth).toUpperCase();
    }

}
