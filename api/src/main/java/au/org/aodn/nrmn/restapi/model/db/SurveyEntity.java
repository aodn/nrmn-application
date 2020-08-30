package au.org.aodn.nrmn.restapi.model.db;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "survey"  )
@EqualsAndHashCode
@Audited(withModifiedFlag = true)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class SurveyEntity {

    @Id
    @Column(name = "survey_id")
    private int surveyId;

    @Basic
    @Column(name = "survey_date")
    private Date surveyDate;

    @Basic
    @Column(name = "survey_time")
    private Time surveyTime;

    @Basic
    @Column(name = "depth")
    private int depth;

    @Basic
    @Column(name = "survey_num")
    private int surveyNum;

    @Basic
    @Column(name = "visibility")
    private Integer visibility;

    @Basic
    @Column(name = "direction")
    private String direction;

    @Basic
    @Column(name = "survey_attribute")
    @Type(type = "jsonb")
    private Map<String,String> surveyAttribute;

}
