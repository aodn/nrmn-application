package au.org.aodn.nrmn.restapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "diver_ref")
@Audited(withModifiedFlag = true)
public class Diver {
    @Id
    @SequenceGenerator(name = "diver_ref_diver_id", sequenceName = "diver_ref_diver_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "diver_id", unique = true, updatable = false, nullable = false)
    private int diverId;

    @Column(name = "initials")
    private String initials;

    @Column(name = "full_name")
    private String fullName;
}
