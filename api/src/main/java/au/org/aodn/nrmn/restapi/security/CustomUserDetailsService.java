package au.org.aodn.nrmn.restapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.org.aodn.nrmn.restapi.data.model.SecUser;
import au.org.aodn.nrmn.restapi.data.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.enums.SecUserStatus;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    SecUserRepository userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        SecUser user = userRepo
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email)
                );

        if (user.getStatus() != SecUserStatus.ACTIVE) {
            throw new UserNotActiveException("user is not active: " + email);
        }

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        SecUser user = userRepo.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );
        return UserPrincipal.create(user);
    }
}
