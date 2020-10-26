package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "sec_role"  )
@Schema(title = "Add Role")
public class SecRoleEntity {
    public SecRoleEntity(SecRoleName status) {
        this.name = status;
    }

    @Id
    @Column(name="name", unique=true, updatable=false, nullable=false)
    @Enumerated(EnumType.STRING)
    private SecRoleName name;
    @Version
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "version", nullable = false)
    private Integer version;

}
