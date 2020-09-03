package au.org.aodn.nrmn.restapi.model.db;

import javax.persistence.*;

@Entity
@Table(name = "measure_type_ref"  )
public class MeasureTypeRefEntity {
    private int measureTypeId;
    private String measureTypeName;
    private Boolean isActive;

    @Id
    @Column(name = "measure_type_id")
    public int getMeasureTypeId() {
        return measureTypeId;
    }

    public void setMeasureTypeId(int measureTypeId) {
        this.measureTypeId = measureTypeId;
    }

    @Basic
    @Column(name = "measure_type_name")
    public String getMeasureTypeName() {
        return measureTypeName;
    }

    public void setMeasureTypeName(String measureTypeName) {
        this.measureTypeName = measureTypeName;
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

        MeasureTypeRefEntity that = (MeasureTypeRefEntity) o;

        if (measureTypeId != that.measureTypeId) return false;
        if (measureTypeName != null ? !measureTypeName.equals(that.measureTypeName) : that.measureTypeName != null)
            return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = measureTypeId;
        result = 31 * result + (measureTypeName != null ? measureTypeName.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        return result;
    }
}
