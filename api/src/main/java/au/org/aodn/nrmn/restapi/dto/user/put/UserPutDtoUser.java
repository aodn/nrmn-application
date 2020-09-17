package au.org.aodn.nrmn.restapi.dto.user.put;

import javax.validation.constraints.*;

public class UserPutDtoUser {

    @NotBlank
    @Size(min = 3, max = 15)
    private String name;

    @NotBlank
    @Size(max = 40)
    @Email
    private String emailAddress;

    private String oldPassword;

    @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*?[a-zA-Z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).{8,20}$",
            message = "must be 8-20 characters, containing at least 1 letter, 1 number and 1 special character")
    private String newPassword;

    @NotBlank
    @Size(min = 6, max = 20)
    private String phoneNumber;

    @NotNull
    private Long organisationId;

    @NotNull
    private Long version;

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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
