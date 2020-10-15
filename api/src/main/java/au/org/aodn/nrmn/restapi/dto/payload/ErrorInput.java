package au.org.aodn.nrmn.restapi.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

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
