package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "sec_role")
public class SecRoleEntity {
    public SecRoleEntity(SecRoleName status) {
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
