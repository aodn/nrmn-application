package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import lombok.*;

import javax.persistence.*;
import java.nio.file.FileStore;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "sec_role")
public class SecRoleEntity {

    @Id
    @SequenceGenerator(name = "role_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private SecRoleName name;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

}
