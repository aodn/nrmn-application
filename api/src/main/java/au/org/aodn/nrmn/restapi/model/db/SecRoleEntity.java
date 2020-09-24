package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "sec_role"  )
public class SecRoleEntity {
    @Id
    @Column(name="name", unique=true, updatable=false, nullable=false)
    @Enumerated(EnumType.STRING)
    private SecRoleName name;

    @Version
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "version", nullable = false)
    private Integer version;

}
