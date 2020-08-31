package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.composedID.RawSurveyID;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "raw_survey", schema = "nrmn", catalog = "nrmn")
public class StagedSurveyEntity {
    @EmbeddedId
    @JsonUnwrapped
    private RawSurveyID rid;

    @JsonProperty(value = "Site No")
    @Column(name = "site_no")
    private String SiteNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Column(name = "date")
    private Date Date;

    @Column(name = "diver")
    private String Diver;

    @Column(name = "depth")
    private Double Depth;

    @Column(name = "method")
    private Integer Method;

    @Column(name = "block")
    private Integer Block;

    @Column(name = "species")
    private String Species;

    @Column(name = "buddy")
    private String Buddy;

    @JsonProperty(value = "Site Name")
    @Column(name = "site_name")
    private String SiteName;

    @Column(name = "longitude")
    private Double Longitude;

    @Column(name = "latitude")
    private Double Latitude;

    @Column(name = "vis")
    private Integer vis;

    @Column(name = "direction")
    private String Direction;

    @Column(name = "time")
    private Double Time;

    @JsonProperty(value = "P-Qs")
    @Column(name = "PQs")
    private Integer PQs;

    @Column(name = "code")
    private String Code;

    @JsonProperty(value = "Common name")
    @Column(name = "common_name")
    private String CommonName;

    @Column(name = "total")
    private Integer Total;

    @Column(name = "inverts")
    private Integer Inverts;

    @Column(name = "m2_invert_sizing_species")
    private Boolean M2InvertSizingSpecies;

    @Column(name = "L5")
    private Integer L5;

    @Column(name = "L95")
    private Integer L95;

    @Column(name = "is_invert_Sizing")
    private Boolean IsInvertSizing;

    @Column(name = "measureValue", columnDefinition = "json")
    @Type(type = "jsonb")
    private Map<String, Double> MeasureJson;

    @OneToMany
    private List<ErrorCheckEntity> Errors;
}