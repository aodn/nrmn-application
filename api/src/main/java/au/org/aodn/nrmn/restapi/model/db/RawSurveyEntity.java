package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.composedID.RawSurveyID;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "raw_survey", schema = "nrmn", catalog = "nrmn")
public class RawSurveyEntity {
    @EmbeddedId
    @JsonUnwrapped
    public RawSurveyID rid;

    @JsonProperty(value = "Site No")
    @Column(name = "site_no")
    public String SiteNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Column(name = "date")
    public Date Date;

    @Column(name = "diver")
    public String Diver;

    @Column(name = "depth")
    public Double Depth;

    @Column(name = "method")
    public Integer Method;

    @Column(name = "block")
    public Integer Block;

    @Column(name = "species")
    public String Species;

    @Column(name = "buddy")
    public String Buddy;

    @JsonProperty(value = "Site Name")
    @Column(name = "site_name")
    public String SiteName;

    @Column(name = "longitude")
    public Double Longitude;

    @Column(name = "latitude")
    public Double Latitude;

    @Column(name = "vis")
    public Integer vis;

    @Column(name = "direction")
    public String Direction;

    @Column(name = "time")
    public Double Time;

    @JsonProperty(value = "P-Qs")
    @Column(name = "PQs")
    public Integer PQs;

    @Column(name = "code")
    public String Code;

    @JsonProperty(value = "Common name")
    @Column(name = "common_name")
    public String CommonName;

    @Column(name = "total")
    public Integer Total;

    @Column(name = "inverts")
    public Integer Inverts;

    @Column(name = "m2_invert_sizing_species")
    public Boolean M2InvertSizingSpecies;

    @Column(name = "L5")
    public Integer L5;

    @Column(name = "L95")
    public Integer L95;

    @Column(name = "is_invert_Sizing")
    public Boolean IsInvertSizing;

    @Column(name = "measureValue", columnDefinition = "json")
    @Type(type = "jsonb")
    public Map<String, Double> MeasureJson;

    @OneToMany
    public List<ErrorCheckEntity> Errors;
}