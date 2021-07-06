package au.org.aodn.nrmn.restapi.model.db;

import java.util.Collections;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.model.db.SecUser.SecUserBuilder;
import au.org.aodn.nrmn.restapi.model.db.enums.SecUserStatus;
import au.org.aodn.nrmn.restapi.repository.SecRoleRepository;
import au.org.aodn.nrmn.restapi.repository.SecUserRepository;
import lombok.val;

@Component
public class SecUserTestData {

    @Autowired
    SecUserRepository userRepo;

    @Autowired
    SecRoleTestData roleTestData;
    @Autowired
    SecRoleRepository roleRepo;

    private int userNo = 0;

    public SecUser persistedUser() {
        val user = defaultBuilder().build();
        return userRepo.saveAndFlush(user);
    }

    public SecUserBuilder defaultBuilder() {
        val role = roleTestData.persistedRole();

        return SecUser.builder()
                .email("builder" + ++userNo + "@test.com")
                .fullName("IT-user" + userNo)
                .hashedPassword("pwdhashed123")
                .roles(new HashSet<>(Collections.singletonList(role)))
                .status(SecUserStatus.ACTIVE);
    }


}
