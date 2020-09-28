package au.org.aodn.nrmn.restapi.model.db;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title="Add Location")
@Table(name = "location_ref"  )
public class LocationRefEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "location_id")
    private int locationId;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "is_active")
    private Boolean isActive;
}
