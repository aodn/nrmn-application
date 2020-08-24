package au.org.aodn.nrmn.restapi.model.db.audit;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.core.Authentication;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "user_action_aud", schema = "nrmn", catalog = "nrmn")
public class UserActionAuditEntity {
    private long id;
    private String requestId;
    private String username;
    private String operation;
    private String details;
    private Timestamp auditTime;

    public UserActionAuditEntity() {}

    public UserActionAuditEntity(String operation, String details, Authentication authentication) {
        auditTime = new Timestamp(System.currentTimeMillis());
        this.operation = operation;
        this.username = authentication.getName();
        this.requestId = ThreadContext.get("fishtag");
        this.details = details;
    }

    @Id
    @SequenceGenerator(name="entity_id_seq", sequenceName="hibernate_sequence", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "request_id")
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String sql) {
        this.requestId = sql;
    }

    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String result) {
        this.username = result;
    }

    @Basic
    @Column(name = "operation")
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Basic
    @Column(name = "details", columnDefinition="text")
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Basic
    @Column(name = "audit_time", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    public Timestamp getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Timestamp timestamp) {
        this.auditTime = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserActionAuditEntity that = (UserActionAuditEntity) o;
        return id == that.id &&
                Objects.equals(requestId, that.requestId) &&
                Objects.equals(username, that.username) &&
                Objects.equals(operation, that.operation) &&
                Objects.equals(details, that.details) &&
                Objects.equals(auditTime, that.auditTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestId, username, operation, details, auditTime);
    }

}
