package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import au.org.aodn.nrmn.restapi.model.db.enums.UserSecStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sec_user", uniqueConstraints ={@UniqueConstraint( name = "UNIQUE_EMAIL",columnNames = {"email_address"})} )
@EqualsAndHashCode
@Getter
@Setter
@Schema(title="Add a User")
@RepositoryRestResource(collectionResourceRel = "people", path = "people")
@Audited(withModifiedFlag = true)
public class SecUserEntity {

    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @SequenceGenerator(name = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int userId;
    public long getId() {
        return userId;
    }

    @Column(name = "full_name")
    private String username;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "hashed_password")
    private String hashedPassword;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "is_active", nullable=false)
//    private UserSecStatus status;


    @NotAudited
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "sec_user_sec_role",
            joinColumns = @JoinColumn(name = "sec_user_id",foreignKey=@ForeignKey(name="FK_USER_SEC_ROLE")),
            inverseJoinColumns = @JoinColumn(name = "sec_role_name", foreignKey=@ForeignKey(name="FK_ROLE_USER_SEC")))
    private Set<SecRoleEntity> roles = new HashSet<>();

    // todo should be automatically  created
    public void setPasswordHash(String passwordHash) {
        this.hashedPassword = passwordHash;
    }

    @Transient
    public Set<String> getRolesAsStringSet() {
        return this.roles.stream().map(temp -> temp.getName().name()).collect(Collectors.toSet());
    }

}
