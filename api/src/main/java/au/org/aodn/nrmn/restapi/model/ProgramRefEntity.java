package au.org.aodn.nrmn.restapi.model;

import javax.persistence.*;

@Entity
@Table(name = "program_ref", schema = "nrmn", catalog = "nrmn")
public class ProgramRefEntity {
    private int programId;
    private String programName;
    private Boolean isActive;

    @Id
    @Column(name = "program_id")
    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    @Basic
    @Column(name = "program_name")
    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
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

        ProgramRefEntity that = (ProgramRefEntity) o;

        if (programId != that.programId) return false;
        if (programName != null ? !programName.equals(that.programName) : that.programName != null) return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = programId;
        result = 31 * result + (programName != null ? programName.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        return result;
    }
}
