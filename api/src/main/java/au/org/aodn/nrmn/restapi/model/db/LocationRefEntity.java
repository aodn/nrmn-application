package au.org.aodn.nrmn.restapi.model.db;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited(withModifiedFlag = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "location_ref"  )
public class LocationRefEntity  {
    @Id
    @Column(name = "location_id")
    private int locationId;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "is_active")
    private Boolean isActive;
}
