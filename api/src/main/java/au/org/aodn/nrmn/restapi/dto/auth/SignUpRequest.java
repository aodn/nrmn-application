package au.org.aodn.nrmn.restapi.dto.auth;

import au.org.aodn.nrmn.restapi.model.db.SecRoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank
    @Email(message="Please provide a valid email address")
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(min = 3, max = 30, message = "fullname must be between 3 and 30 characters")
    private String fullname;

    @NotBlank
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*?[a-zA-Z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).{8,20}$",
            message = "must be 8-20 characters, containing at least 1 letter, 1 number and 1 special character")
    private String password;

    private List<SecRoleEntity> roles;

}
