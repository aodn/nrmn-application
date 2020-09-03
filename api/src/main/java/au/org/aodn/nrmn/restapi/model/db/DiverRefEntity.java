package au.org.aodn.nrmn.restapi.model.db;

import lombok.*;
import org.hibernate.envers.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Audited(withModifiedFlag = true)
@Table(name = "diver_ref")
@EqualsAndHashCode
public class DiverRefEntity {
    @Id
    @Column(name = "diver_id")
    private int diverId;

    @Column(name = "initials")
    private String initials;

    @Column(name = "full_name")
    private String fullName;
}
