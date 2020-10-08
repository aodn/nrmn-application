package au.org.aodn.nrmn.restapi.dto.payload;

import au.org.aodn.nrmn.restapi.model.db.SecRoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInput {
    @NotBlank
    private String message;

    @NotBlank
    private String  field;

}
