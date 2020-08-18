package au.org.aodn.nrmn.restapi.model.db;

import javax.persistence.*;

@Entity
@Table(name = "method_ref", schema = "nrmn", catalog = "nrmn")
public class MethodRefEntity {
    private int methodId;
    private String methodName;
    private Boolean isActive;

    @Id
    @Column(name = "method_id")
    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    @Basic
    @Column(name = "method_name")
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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

        MethodRefEntity that = (MethodRefEntity) o;

        if (methodId != that.methodId) return false;
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = methodId;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        return result;
    }
}
