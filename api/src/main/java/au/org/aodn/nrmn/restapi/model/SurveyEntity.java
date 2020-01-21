package au.org.aodn.nrmn.restapi.model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "survey", schema = "nrmn", catalog = "nrmn")
public class SurveyEntity {
    private int surveyId;
    private Date surveyDate;
    private Time surveyTime;
    private int depth;
    private int surveyNum;
    private Integer visibility;
    private String direction;
    private String surveyAttribute;
    private boolean isControl;
    private boolean published;

    @Id
    @Column(name = "survey_id")
    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    @Basic
    @Column(name = "survey_date")
    public Date getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(Date surveyDate) {
        this.surveyDate = surveyDate;
    }

    @Basic
    @Column(name = "survey_time")
    public Time getSurveyTime() {
        return surveyTime;
    }

    public void setSurveyTime(Time surveyTime) {
        this.surveyTime = surveyTime;
    }

    @Basic
    @Column(name = "depth")
    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Basic
    @Column(name = "survey_num")
    public int getSurveyNum() {
        return surveyNum;
    }

    public void setSurveyNum(int surveyNum) {
        this.surveyNum = surveyNum;
    }

    @Basic
    @Column(name = "visibility")
    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    @Basic
    @Column(name = "direction")
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Basic
    @Column(name = "survey_attribute")
    public String getSurveyAttribute() {
        return surveyAttribute;
    }

    public void setSurveyAttribute(String surveyAttribute) {
        this.surveyAttribute = surveyAttribute;
    }

    @Basic
    @Column(name = "is_control")
    public boolean isControl() {
        return isControl;
    }

    public void setControl(boolean control) {
        isControl = control;
    }

    @Basic
    @Column(name = "published")
    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SurveyEntity that = (SurveyEntity) o;

        if (surveyId != that.surveyId) return false;
        if (depth != that.depth) return false;
        if (surveyNum != that.surveyNum) return false;
        if (isControl != that.isControl) return false;
        if (published != that.published) return false;
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
        result = 31 * result + (isControl ? 1 : 0);
        result = 31 * result + (published ? 1 : 0);
        return result;
    }
}
