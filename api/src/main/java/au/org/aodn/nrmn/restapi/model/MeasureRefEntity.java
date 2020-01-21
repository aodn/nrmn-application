package au.org.aodn.nrmn.restapi.model;

import javax.persistence.*;

@Entity
@Table(name = "measure_ref", schema = "nrmn", catalog = "nrmn")
public class MeasureRefEntity {
    private int measureId;
    private String measureName;
    private Integer seqNo;
    private Boolean isActive;

    @Id
    @Column(name = "measure_id")
    public int getMeasureId() {
        return measureId;
    }

    public void setMeasureId(int measureId) {
        this.measureId = measureId;
    }

    @Basic
    @Column(name = "measure_name")
    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    @Basic
    @Column(name = "seq_no")
    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    @Basic
    @Column(name = "is_active")
    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasureRefEntity that = (MeasureRefEntity) o;

        if (measureId != that.measureId) return false;
        if (measureName != null ? !measureName.equals(that.measureName) : that.measureName != null) return false;
        if (seqNo != null ? !seqNo.equals(that.seqNo) : that.seqNo != null) return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = measureId;
        result = 31 * result + (measureName != null ? measureName.hashCode() : 0);
        result = 31 * result + (seqNo != null ? seqNo.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        return result;
    }
}
