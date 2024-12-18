package au.org.aodn.nrmn.restapi.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import au.org.aodn.nrmn.restapi.enums.SecRoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Data
@Cache(region = "entities", usage = CacheConcurrencyStrategy.READ_WRITE)
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "sec_role")
public class SecRole implements Serializable {

    @Id
    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private SecRoleName name;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
