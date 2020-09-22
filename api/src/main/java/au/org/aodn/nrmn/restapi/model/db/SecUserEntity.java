package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.UserSecStatus;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.Reference;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sec_user", uniqueConstraints = {@UniqueConstraint(name = "UNIQUE_EMAIL", columnNames = {"email_address"})})
@EqualsAndHashCode
@Getter
@Setter
@Audited(withModifiedFlag = true)
@Schema(title="Add User")
public class SecUserEntity {

    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @SequenceGenerator(name = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int userId;

    @Version
    @Column(name = "version", nullable = false)
    @Schema(format = "hidden", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer version;


    @Column(name = "full_name")
    @Size(min = 2, max = 100)
    @NotNull
    private String fullName;

    @Column(name = "email_address", nullable = false)
  //  @Pattern(message = "wrong email format", regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")
    @NotNull
    @NotBlank
    @Schema(format = "email")
    private String email;

    @Column(name = "hashed_password")
    @Size(min= 5, max = 100)
    @Schema(title =  "password")
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserSecStatus status;

    @NotAudited
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "sec_user_sec_role",
            joinColumns = @JoinColumn(name = "sec_user_id", foreignKey = @ForeignKey(name = "FK_USER_SEC_ROLE")),
            inverseJoinColumns = @JoinColumn(name = "sec_role_name", foreignKey = @ForeignKey(name = "FK_ROLE_USER_SEC")))
    private Set<SecRoleEntity> roles = new HashSet<>();

}
