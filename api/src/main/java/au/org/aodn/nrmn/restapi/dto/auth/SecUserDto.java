package au.org.aodn.nrmn.restapi.dto.auth;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Id;

import au.org.aodn.nrmn.restapi.data.model.SecUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecUserDto {

    @Id
    private Long userId;
    private String fullName;
    private String expires;
    private String email;
    private List<String> roles;
    private boolean resetPassword;
    private String newPassword;
    private String error;

    public SecUserDto(SecUser user, String newPassword)
    {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        var expires = user.getExpires();
        this.expires = Objects.nonNull(expires) ? expires.format(DateTimeFormatter.BASIC_ISO_DATE) : "";
        this.roles = Objects.nonNull(user.getRoles()) ? user.getRoles().stream().map(r -> r.getName().toString()).collect(Collectors.toList()) : List.<String>of();
        this.newPassword = Objects.nonNull(newPassword) && newPassword.length() > 0 ? newPassword : null;
    }
}
