package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Table(name = "sec_role"  )
public class SecRoleEntity {

    @Id
    @SequenceGenerator(name="role_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "role_id_seq")
    @Column(name="id", unique=true, updatable=false, nullable=false)
    @Getter @Setter private Long id;

    @Version
    @Column(name = "version", nullable = false)
    @Getter @Setter private Integer version;

    @Column(name = "name", nullable=false)
    @Enumerated(EnumType.STRING)
    @Getter @Setter private SecRoleName name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecRoleEntity that = (SecRoleEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
