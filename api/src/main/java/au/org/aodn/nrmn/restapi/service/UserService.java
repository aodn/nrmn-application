package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequest;
import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.SecRoleEntity;
import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedJobEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import au.org.aodn.nrmn.restapi.model.db.enums.SecUserStatus;
import au.org.aodn.nrmn.restapi.repository.*;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.lang.model.type.ErrorType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService extends ValidatorHelpers {
    @Value("${app.passwordResetExpiryMinutes}")
    String passwordResetExpiryMinutes;

    @Autowired
    SecRoleEntityRepository roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SecUserEntityRepository userRepo;

    public Validated<ErrorInput, SecUserEntity> createUser(SignUpRequest signupReq) {
        val emailValid =
                duplicateValid(userRepo.findByEmail(signupReq.getEmail()), "email");
        val errorList = toErrorList(emailValid);
        if (errorList.isEmpty()) {
            val userRole = roleRepo
                    .findByName(SecRoleName.ROLE_USER)
                    .orElseGet(() -> {
                        val role = new SecRoleEntity(SecRoleName.ROLE_USER);
                        return roleRepo.save(role);
                    });
            val user = new SecUserEntity(
                    signupReq.getFullname(),
                    signupReq.getEmail(),
                    passwordEncoder.encode(signupReq.getPassword()),
                    SecUserStatus.ACTIVE,
                    Arrays.asList(userRole));
            return Validated.valid(userRepo.save(user));
        }
        return Validated.invalid(errorList.get(0));

    }

}
