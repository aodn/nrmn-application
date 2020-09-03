package au.org.aodn.nrmn.restapi.model.db;

import javax.persistence.*;

@Entity
@Table(name = "obs_item_type_ref"  )
public class ObsItemTypeRefEntity {
    private int obsItemTypeId;
    private String obsItemTypeName;
    private Boolean isActive;

    @Id
    @Column(name = "obs_item_type_id")
    public int getObsItemTypeId() {
        return obsItemTypeId;
    }

    public void setObsItemTypeId(int obsItemTypeId) {
        this.obsItemTypeId = obsItemTypeId;
    }

    @Basic
    @Column(name = "obs_item_type_name")
    public String getObsItemTypeName() {
        return obsItemTypeName;
    }

    public void setObsItemTypeName(String obsItemTypeName) {
        this.obsItemTypeName = obsItemTypeName;
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

        ObsItemTypeRefEntity that = (ObsItemTypeRefEntity) o;

        if (obsItemTypeId != that.obsItemTypeId) return false;
        if (obsItemTypeName != null ? !obsItemTypeName.equals(that.obsItemTypeName) : that.obsItemTypeName != null)
            return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = obsItemTypeId;
        result = 31 * result + (obsItemTypeName != null ? obsItemTypeName.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        return result;
    }
}
