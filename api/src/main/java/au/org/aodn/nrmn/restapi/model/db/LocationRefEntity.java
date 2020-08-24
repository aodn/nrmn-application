package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.audit.DateAudit;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited(withModifiedFlag = true)
@Table(name = "location_ref", schema = "nrmn", catalog = "nrmn")
public class LocationRefEntity extends DateAudit {
    private int locationId;
    private String locationName;
    private Boolean isActive;

    @Id
    @Column(name = "location_id")
    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    @Basic
    @Column(name = "location_name")
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

        LocationRefEntity that = (LocationRefEntity) o;

        if (locationId != that.locationId) return false;
        if (locationName != null ? !locationName.equals(that.locationName) : that.locationName != null) return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locationId;
        result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        return result;
    }
}
