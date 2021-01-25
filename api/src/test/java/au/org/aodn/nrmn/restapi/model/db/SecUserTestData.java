package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SecUserStatus;
import au.org.aodn.nrmn.restapi.repository.SecRoleRepository;
import au.org.aodn.nrmn.restapi.repository.SecUserRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import au.org.aodn.nrmn.restapi.model.db.SecUser.SecUserBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class SecUserTestData {

    @Autowired
    SecUserRepository userRepo;

    @Autowired
    SecRoleTestData roleTestData;
    @Autowired
    SecRoleRepository roleRepo;

    public SecUser persistedUser() {
        val user = defaultBuilder().build();
        return userRepo.saveAndFlush(user);
    }

    public SecUserBuilder defaultBuilder() {
        val role = roleTestData.persistedRole();

        return SecUser.builder()
                .email("builder@test.com")
                .fullName("IT-user")
                .hashedPassword("pwdhashed123")
                .roles(new HashSet<>(Collections.singletonList(role)))
                .status(SecUserStatus.ACTIVE);
    }


}
