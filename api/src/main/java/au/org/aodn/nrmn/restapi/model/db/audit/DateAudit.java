package au.org.aodn.nrmn.restapi.model.db.audit;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)

public abstract class DateAudit implements Serializable {

    @CreatedDate
    @Column(nullable = true, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Timestamp createdAt;

    @LastModifiedDate
    @Column(nullable = true, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Timestamp lastUpdated;

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated){
        this.lastUpdated = lastUpdated;
    }
}