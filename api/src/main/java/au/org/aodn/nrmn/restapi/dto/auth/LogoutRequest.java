package au.org.aodn.nrmn.restapi.dto.auth;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LogoutRequest {
    @NotBlank
    private String username;
}
