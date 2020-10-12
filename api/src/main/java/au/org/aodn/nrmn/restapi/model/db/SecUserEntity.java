package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecUserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sec_user", uniqueConstraints ={@UniqueConstraint( name = "UNIQUE_EMAIL",columnNames = {"email_address"})} )
@EqualsAndHashCode
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class SecUserEntity {

    @Id
    @SequenceGenerator(name="user_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "user_id_seq")
    @Column(name="id", unique=true, updatable=false, nullable=false)
   private Long userId;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;


    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email_address", nullable = false)
    private String email;
    @JsonIgnore
    @Column(name = "hashed_password")
    private String hashedPassword;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable=false)
    private SecUserStatus status;

    public SecUserEntity(String fullName, String email, String password, SecUserStatus status, List<SecRoleEntity> roles ) {
        this.fullName = fullName;
        this.email = email;
        this.hashedPassword = password;
        this.status = status;
        this.roles = new HashSet<>(roles); ;
    }

    @NotAudited
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "sec_user_roles",
            joinColumns = @JoinColumn(name = "sec_user_id",foreignKey=@ForeignKey(name="FK_USER_SEC_ROLE")),
            inverseJoinColumns = @JoinColumn(name = "sec_role_id", foreignKey=@ForeignKey(name="FK_ROLE_USER_SEC")))
     private Set<SecRoleEntity> roles = new HashSet<>();

}
