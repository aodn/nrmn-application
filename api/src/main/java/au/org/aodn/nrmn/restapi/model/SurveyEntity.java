package au.org.aodn.nrmn.restapi.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "survey")
public class SurveyEntity {
    @Id
    @Column(name = "survey_id")
    public int surveyId;

    @Basic
    @Column(name = "survey_date")
    public Date surveyDate;

    @Basic
    @Column(name = "survey_time")
    public Time surveyTime;

    @Basic
    @Column(name = "depth")
    public int depth;

    @Basic
    @Column(name = "survey_num")
    public int surveyNum;

    @Basic
    @Column(name = "visibility")
    public Integer visibility;

    @Basic
    @Column(name = "direction")
    public String direction;

    @Basic
    @Column(name = "survey_attribute")
    @Type(type = "jsonb")
    public String surveyAttribute;




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SurveyEntity that = (SurveyEntity) o;

        if (surveyId != that.surveyId) return false;
        if (depth != that.depth) return false;
        if (surveyNum != that.surveyNum) return false;
        if (surveyDate != null ? !surveyDate.equals(that.surveyDate) : that.surveyDate != null) return false;
        if (surveyTime != null ? !surveyTime.equals(that.surveyTime) : that.surveyTime != null) return false;
        if (visibility != null ? !visibility.equals(that.visibility) : that.visibility != null) return false;
        if (direction != null ? !direction.equals(that.direction) : that.direction != null) return false;
        if (surveyAttribute != null ? !surveyAttribute.equals(that.surveyAttribute) : that.surveyAttribute != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = surveyId;
        result = 31 * result + (surveyDate != null ? surveyDate.hashCode() : 0);
        result = 31 * result + (surveyTime != null ? surveyTime.hashCode() : 0);
        result = 31 * result + depth;
        result = 31 * result + surveyNum;
        result = 31 * result + (visibility != null ? visibility.hashCode() : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (surveyAttribute != null ? surveyAttribute.hashCode() : 0);
        return result;
    }
}
