package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.audit.DateAudit;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited(withModifiedFlag = true)
@Table(name = "observation", schema = "nrmn", catalog = "nrmn")
public class ObservationEntity extends DateAudit {
    private int observationId;
    private Integer measureValue;
    private String observationAttribute;

    @Id
    @Column(name = "observation_id")
    public int getObservationId() {
        return observationId;
    }

    public void setObservationId(int observationId) {
        this.observationId = observationId;
    }

    @Basic
    @Column(name = "measure_value")
    public Integer getMeasureValue() {
        return measureValue;
    }

    public void setMeasureValue(Integer measureValue) {
        this.measureValue = measureValue;
    }

    @Basic
    @Column(name = "observation_attribute")
    public String getObservationAttribute() {
        return observationAttribute;
    }

    public void setObservationAttribute(String observationAttribute) {
        this.observationAttribute = observationAttribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObservationEntity that = (ObservationEntity) o;

        if (observationId != that.observationId) return false;
        if (measureValue != null ? !measureValue.equals(that.measureValue) : that.measureValue != null) return false;
        if (observationAttribute != null ? !observationAttribute.equals(that.observationAttribute) : that.observationAttribute != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = observationId;
        result = 31 * result + (measureValue != null ? measureValue.hashCode() : 0);
        result = 31 * result + (observationAttribute != null ? observationAttribute.hashCode() : 0);
        return result;
    }
}
