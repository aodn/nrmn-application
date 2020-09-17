package au.org.aodn.nrmn.restapi.dto.auth;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SignUpRequestDto {

    @NotBlank
    @Size(min = 3, max = 15, message = "username must be between 3 and 15 characters")
    private String name;

    @NotBlank
    @Size(max = 254)
    @Email
    private String emailAddress;

    @NotBlank
    @Size(min = 8, max = 20)
//    @Pattern(regexp = "^(?=.*?[a-zA-Z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).{8,20}$",
//            message = "must be 8-20 characters, containing at least 1 letter, 1 number and 1 special character")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
