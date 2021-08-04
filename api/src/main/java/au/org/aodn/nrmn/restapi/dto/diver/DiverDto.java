package au.org.aodn.nrmn.restapi.dto.diver;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class DiverDto {

    @Id
    @Schema(title = "Diver ID")
    private Integer diverId;

    @NotEmpty
    @Schema(title = "Initials")
    private String initials;

    @NotEmpty
    @Schema(title = "Full Name")
    private String fullName;

    public DiverDto(Diver diver) {
        this.diverId = diver.getDiverId();
        this.initials = diver.getInitials();
        this.fullName = diver.getFullName();
    }
}
