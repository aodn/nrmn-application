package au.org.aodn.nrmn.restapi.test;

import au.org.aodn.nrmn.restapi.security.JwtTokenProvider;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Component
public class JwtToken {
    @Autowired
    private JwtTokenProvider jwtProvider;

    public String get() {
        val auth = getContext().getAuthentication();
        return jwtProvider.generateToken(auth);
    }
    
}
