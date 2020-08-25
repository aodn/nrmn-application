package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Table(name = "user_sec_role", schema = "nrmn", catalog = "nrmn")
public class UserSecRoleEntity {

    @Id
    @SequenceGenerator(name="entity_id_seq", sequenceName="hibernate_sequence", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
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

        UserSecRoleEntity that = (UserSecRoleEntity) o;

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
