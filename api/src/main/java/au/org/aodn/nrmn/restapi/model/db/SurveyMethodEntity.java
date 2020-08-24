package au.org.aodn.nrmn.restapi.model.db;

import javax.persistence.*;

@Entity
@Table(name = "survey_method", schema = "nrmn", catalog = "nrmn")
public class SurveyMethodEntity {
    private int surveyMethodId;
    private Integer blockNum;
    private Boolean surveyNotDone;

    @Id
    @Column(name = "survey_method_id")
    public int getSurveyMethodId() {
        return surveyMethodId;
    }

    public void setSurveyMethodId(int surveyMethodId) {
        this.surveyMethodId = surveyMethodId;
    }

    @Basic
    @Column(name = "block_num")
    public Integer getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(Integer blockNum) {
        this.blockNum = blockNum;
    }

    @Basic
    @Column(name = "survey_not_done")
    public Boolean getSurveyNotDone() {
        return surveyNotDone;
    }

    public void setSurveyNotDone(Boolean surveyNotDone) {
        this.surveyNotDone = surveyNotDone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SurveyMethodEntity that = (SurveyMethodEntity) o;

        if (surveyMethodId != that.surveyMethodId) return false;
        if (blockNum != null ? !blockNum.equals(that.blockNum) : that.blockNum != null) return false;
        if (surveyNotDone != null ? !surveyNotDone.equals(that.surveyNotDone) : that.surveyNotDone != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = surveyMethodId;
        result = 31 * result + (blockNum != null ? blockNum.hashCode() : 0);
        result = 31 * result + (surveyNotDone != null ? surveyNotDone.hashCode() : 0);
        return result;
    }
}
