package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import au.org.aodn.nrmn.restapi.repository.SecRoleRepository;

@Component
public class SecRoleTestData {

    @Autowired
    SecRoleRepository roleRepo;

    public SecRole persistedRole() {
        SecRole role = roleRepo.findByName(SecRoleName.ROLE_ADMIN).get();
        return role;
    }

}
