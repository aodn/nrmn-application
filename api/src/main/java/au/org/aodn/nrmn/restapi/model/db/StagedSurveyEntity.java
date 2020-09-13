package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "staged_survey")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class StagedSurveyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private long id;

    @JsonProperty(value = "Site No")
    @Column(name = "site_no")
    private String siteNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Column(name = "date")
    private Date date;

    @Column(name = "diver")
    private String diver;

    @Column(name = "depth")
    private Double depth;

    @Column(name = "method")
    private Integer method;

    @Column(name = "block")
    private Integer block;

    @Column(name = "species")
    private String species;

    @Column(name = "buddy")
    private String buddy;

    @JsonProperty(value = "Site Name")
    @Column(name = "site_name")
    private String siteName;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "vis")
    private Integer vis;

    @Column(name = "direction")
    private String direction;

    @JsonProperty(value = "P-Qs")
    @Column(name = "PQs")
    private String PQs;

    @Column(name = "code")
    private String code;

    @JsonProperty(value = "Common name")
    @Column(name = "common_name")
    private String CmmonName;

    @Column(name = "total")
    private Integer total;

    @Column(name = "inverts")
    private Integer inverts;

    @Column(name = "m2_invert_sizing_species")
    private Boolean m2InvertSizingSpecies;

    @Column(name = "L5")
    private Integer L5;

    @Column(name = "L95")
    private Integer L95;

    @Column(name = "is_invert_Sizing")
    private Boolean isInvertSizing;

    @Column(name = "Lmax")
    private Integer Lmax;

    @Column(name = "measure_value", columnDefinition = "json")
    @Type(type = "jsonb")
    private Map<String, Integer> measureJson;

    @ManyToOne(fetch = FetchType.LAZY)
    private StagedJobEntity stagedJob;
}