package au.org.aodn.nrmn.restapi.model.db;

import javax.persistence.*;

@Entity
@Table(name = "aphia_rel_type_ref"  )
public class AphiaRelTypeRefEntity {
    private int aphiaRelTypeId;
    private String aphiaRelTypeName;

    @Id
    @Column(name = "aphia_rel_type_id")
    public int getAphiaRelTypeId() {
        return aphiaRelTypeId;
    }

    public void setAphiaRelTypeId(int aphiaRelTypeId) {
        this.aphiaRelTypeId = aphiaRelTypeId;
    }

    @Basic
    @Column(name = "aphia_rel_type_name")
    public String getAphiaRelTypeName() {
        return aphiaRelTypeName;
    }

    public void setAphiaRelTypeName(String aphiaRelTypeName) {
        this.aphiaRelTypeName = aphiaRelTypeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AphiaRelTypeRefEntity that = (AphiaRelTypeRefEntity) o;

        if (aphiaRelTypeId != that.aphiaRelTypeId) return false;
        if (aphiaRelTypeName != null ? !aphiaRelTypeName.equals(that.aphiaRelTypeName) : that.aphiaRelTypeName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = aphiaRelTypeId;
        result = 31 * result + (aphiaRelTypeName != null ? aphiaRelTypeName.hashCode() : 0);
        return result;
    }
}
