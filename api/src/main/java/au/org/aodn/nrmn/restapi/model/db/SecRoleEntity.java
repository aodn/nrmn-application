package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "sec_role"  )
public class SecRoleEntity {

    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @SequenceGenerator(name="role_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "role_id_seq")
    @Column(name="id", unique=true, updatable=false, nullable=false)
  private Long id;

    @Version
    @Hidden
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "name", nullable=false)
    @Enumerated(EnumType.STRING)
    private SecRoleName name;
}
