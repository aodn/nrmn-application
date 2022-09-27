package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.SecRole;
import au.org.aodn.nrmn.restapi.data.repository.SecRoleRepository;
import au.org.aodn.nrmn.restapi.enums.SecRoleName;

@Component
public class SecRoleTestData {

    @Autowired
    SecRoleRepository roleRepo;

    public SecRole persistedRole() {
        SecRole role = roleRepo.findByName(SecRoleName.ROLE_ADMIN).get();
        return role;
    }

}
