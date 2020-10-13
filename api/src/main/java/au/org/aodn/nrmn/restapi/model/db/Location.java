package au.org.aodn.nrmn.restapi.model.db;

import lombok.Data;
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
@Table(name = "location_ref")
@Audited(withModifiedFlag = true)
public class Location {
    @Id
    @SequenceGenerator(name = "location_ref_location_id", sequenceName = "location_ref_location_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "location_id", unique = true, updatable = false, nullable = false)
    private int locationId;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "is_active")
    private Boolean isActive;
}
