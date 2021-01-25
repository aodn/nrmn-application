package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.SecRole.SecRoleBuilder;
import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import au.org.aodn.nrmn.restapi.model.db.enums.SecUserStatus;
import au.org.aodn.nrmn.restapi.repository.SecRoleRepository;
import au.org.aodn.nrmn.restapi.repository.SecUserRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class SecRoleTestData {



    @Autowired
    SecRoleRepository roleRepo;

    public SecRole persistedRole() {
       val role = roleRepo.findByName(SecRoleName.ROLE_ADMIN).get();
        return role;
    }

}
