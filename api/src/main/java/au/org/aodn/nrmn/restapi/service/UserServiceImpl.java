package au.org.aodn.nrmn.restapi.service;

//import au.org.aodn.nrmn.restapi.dto.auth.ChangePasswordRequest;
import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequestDto;
//import au.org.aodn.nrmn.restapi.dto.auth.UserActivationRequest;
import au.org.aodn.nrmn.restapi.dto.user.get.UserGetSimpleDto;
//import au.org.aodn.nrmn.restapi.dto.user.put.UserPutDtoAdmin;
import au.org.aodn.nrmn.restapi.dto.user.put.UserPutDtoUser;
//import au.org.aodn.nrmn.restapi.exception.*;
//import au.org.aodn.nrmn.restapi.model.*;
import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import au.org.aodn.nrmn.restapi.model.db.enums.SecUserStatus;
//import au.org.aodn.nrmn.restapi.payload.PagedResponse;
//import au.org.aodn.nrmn.restapi.repository.*;
import au.org.aodn.nrmn.restapi.exception.ValidationException;
import au.org.aodn.nrmn.restapi.service.util.ValidationUtil;
//import au.org.aodn.nrmn.restapi.util.EmailTemplateManager;
import au.org.aodn.nrmn.restapi.util.UriUtil;
import jdk.internal.dynalink.support.NameCodec;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import au.org.aodn.nrmn.restapi.repository.UserSecEntityRepository;
import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
import au.org.aodn.nrmn.restapi.repository.SecRoleRepository;
import au.org.aodn.nrmn.restapi.repository.SecUserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserSecEntityRepository userSecEntityRepository;

    @Autowired
    SecRoleRepository secRoleRepository;

    @Autowired
    SecUserRepository secUserRepository;

    @Autowired
    private ModelMapper modelMapper;

//    @Autowired
//    PasswordResetTokenRepository passwordResetTokenRepository;

//    @Autowired
//    private EmailTemplateManager emailTemplateManager;


//    @Value("${app.passwordResetExpiryMinutes}")
//    String passwordResetExpiryMinutes;
//
//    @Value("${webapp.page.passwordreset}")
//    String resetPage;
//
//    @Value("${webapp.page.userconfirmation}")
//    String userConfirmationPage;


    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public SecUserEntity createUser(SignUpRequestDto signUpRequestDto, String baseUri) {

        validateSignUpRequest(signUpRequestDto);

        SecUserEntity newSecUserEntity = new SecUserEntity();

        newSecUserEntity.setPasswordHash(passwordEncoder().encode(signUpRequestDto.getPassword()));
        newSecUserEntity.setUsername(signUpRequestDto.getName());
        //newSecUserEntity.setStatus(SecUserStatus.PENDING);
        newSecUserEntity.setEmailAddress(signUpRequestDto.getEmailAddress());
        //newSecUserEntity.addRole(secRoleRepository.findByName(SecRoleName.ROLE_USER.toString()).get());

        secUserRepository.save(newSecUserEntity);



        return newSecUserEntity;
    }


    private void validateSignUpRequest(SignUpRequestDto signUpRequestDto) {
        if(secUserRepository.existsByUsername(signUpRequestDto.getName())) {
            throw new ValidationException(String.format("username '%s' already exists", signUpRequestDto.getName()));
        }

        if(secUserRepository.existsByEmailAddress(signUpRequestDto.getEmailAddress())) {
            throw new ValidationException("email address already in use");
        }

    }
//
//    @Override
//    public void updateUser(UserPutDtoUser userPutDtoUser, Long userId) {
//        SecUserEntity dbUser = secUserRepository.findById(userId).orElseThrow(() ->
//                new ResourceNotFoundException("user", "id", userId));
//
//        ValidationUtil.versionCheck(userPutDtoUser.getVersion(), dbUser.getVersion());
//
//        if (userPutDtoUser.getOldPassword() != null && userPutDtoUser.getNewPassword() != null) {
//            if (!userPutDtoUser.getOldPassword().equals(userPutDtoUser.getNewPassword())) {
//
//                if (passwordEncoder.matches(userPutDtoUser.getOldPassword(), dbUser.getPasswordHash())) {
//                    dbUser.setPasswordHash(passwordEncoder.encode(userPutDtoUser.getNewPassword()));
//                } else {
//                    throw new ValidationException("incorrect password");
//                }
//            }
//        }
//
//        if (!userPutDtoUser.getEmailAddress().equals(dbUser.getEmailAddress())) {
//            if (secUserRepository.findByEmailAddress(userPutDtoUser.getEmailAddress()).isPresent()) {
//                throw new ValidationException("email address already in use");
//            }
//
//            dbUser.setEmailAddress(userPutDtoUser.getEmailAddress());
//        }
//
//        if (!userPutDtoUser.getPhoneNumber().equals(dbUser.getPhoneNumber())) { dbUser.setPhoneNumber(userPutDtoUser.getPhoneNumber());}
//        if (!userPutDtoUser.getName().equals(dbUser.getName())) { dbUser.setName(userPutDtoUser.getName());}
//    }


//    @Override
//    public SecUserEntity updateUser(SecUserEntity secUserEntity) {
//        return secUserRepository.save(secUserEntity);
//    }






}