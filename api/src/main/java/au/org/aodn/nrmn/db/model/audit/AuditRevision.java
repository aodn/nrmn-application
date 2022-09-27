package au.org.aodn.nrmn.db.model.audit;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "REVINFO")
@RevisionEntity(AuditRevisionListener.class)
public class AuditRevision extends DefaultRevisionEntity {

    private static final long serialVersionUID = 3L;

    private String username;

    private String apiRequestId;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApiRequestId() {
        return this.apiRequestId;
    }

    public void setApiRequestId(String apiRequestId) {
        this.apiRequestId = apiRequestId;
    }
}
