package au.org.aodn.nrmn.restapi.dto.location;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import au.org.aodn.nrmn.db.model.Location;

@Data
@NoArgsConstructor
public class LocationDto {

    @Id
    private Integer locationId;

    @NotEmpty
    private String locationName;

    private Boolean isActive;;

    public LocationDto(Location location) {
        this.locationId = location.getLocationId();
        this.locationName = location.getLocationName();
        this.isActive = location.getIsActive();
    }
}
