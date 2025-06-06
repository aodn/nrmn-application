package au.org.aodn.nrmn.restapi.data.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.org.aodn.nrmn.restapi.enums.SecUserStatus;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.cache.annotation.Cacheable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Table(name = "sec_user", uniqueConstraints = {@UniqueConstraint(name = "UNIQUE_EMAIL", columnNames = {"email_address"})})
@Cache(region = "entities", usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited(withModifiedFlag = true)
public class  SecUser implements Serializable {

    @Id
    @SequenceGenerator(name = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long userId;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "expires")
    private LocalDateTime expires;

    @Column(name = "email_address", nullable = false)
    private String email;

    @JsonIgnore
    @Column(name = "hashed_password")
    private String hashedPassword;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SecUserStatus status;

    public SecUser(String fullName, String email, String password, SecUserStatus status, List<SecRole> roles) {
        this.fullName = fullName;
        this.email = email;
        this.hashedPassword = password;
        this.status = status;
        this.roles = new HashSet<>(roles);
    }

    @NotAudited
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sec_user_roles",
        joinColumns = @JoinColumn(name = "sec_user_id", foreignKey = @ForeignKey(name = "FK_USER_SEC_ROLE")),
        inverseJoinColumns = @JoinColumn(name = "sec_role_id", foreignKey = @ForeignKey(name = "FK_ROLE_USER_SEC")))
    private Set<SecRole> roles;

}
