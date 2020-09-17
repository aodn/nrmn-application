package au.org.aodn.nrmn.restapi.service;

//import au.org.aodn.nrmn.restapi.dto.auth.ChangePasswordRequest;
import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequestDto;
//import au.org.aodn.nrmn.restapi.dto.auth.UserActivationRequest;
import au.org.aodn.nrmn.restapi.dto.user.get.UserGetSimpleDto;
//import au.org.aodn.nrmn.restapi.dto.user.put.UserPutDtoAdmin;
import au.org.aodn.nrmn.restapi.dto.user.put.UserPutDtoUser;
//import au.org.aodn.nrmn.restapi.model.PasswordResetTokenEntity;
import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
//import au.org.aodn.nrmn.restapi.payload.PagedResponse;


public interface UserService {
    SecUserEntity createUser(SignUpRequestDto signUpRequestDto, String confirmationUri);
    //void updateUser(UserPutDtoUser userPutDtoUser, Long userId);
//    void adminUpdateUser(UserPutDtoAdmin userPutDtoAdmin);
//   SecUserEntity updateUser(SecUserEntity secUserEntity);
//    PagedResponse<UserGetSimpleDto> getAllUsers(int page, int size, String username, String searchName, Boolean isAdminOrAodnAdmin);
//    SecUserEntity getUserByUsername(String username);
//    SecUserEntity getUserById(Long id);
//    boolean existsByUsername(String username);
//    void sendPasswordResetToken(String usernameOrEmail, String token);
//    //void nullOutToken(PasswordResetTokenEntity tokenEntity);
//    //PasswordResetTokenEntity getPasswordResetTokenByToken(String token);
//    SecUserEntity changePassword(ChangePasswordRequest changePasswordRequest);
//    SecUserEntity activateUser(UserActivationRequest userActivationRequest);
}
