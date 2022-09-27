package au.org.aodn.nrmn.restapi.dto.diver;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import au.org.aodn.nrmn.db.model.Diver;

@Data
@NoArgsConstructor
public class DiverDto {

    @Id
    private Integer diverId;

    @NotEmpty
    private String initials;

    @NotEmpty
    private String fullName;

    public DiverDto(Diver diver) {
        this.diverId = diver.getDiverId();
        this.initials = diver.getInitials();
        this.fullName = diver.getFullName();
    }
}
