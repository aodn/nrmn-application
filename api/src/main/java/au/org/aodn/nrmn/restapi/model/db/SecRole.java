package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Data
@NoArgsConstructor
@Table(name = "sec_role")
public class SecRole {
    public SecRole(SecRoleName status) {
        this.name = status;
    }

    @Id
    @Column(name = "name", unique = true, updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private SecRoleName name;
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
