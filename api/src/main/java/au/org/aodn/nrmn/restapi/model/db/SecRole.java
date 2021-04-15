package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "sec_role")
public class SecRole implements Serializable {
    public SecRole(SecRoleName status) {
        this.name = status;
    }

    @Id
    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private SecRoleName name;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
