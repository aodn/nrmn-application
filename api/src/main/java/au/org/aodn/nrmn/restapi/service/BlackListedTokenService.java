package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BlackListedTokenService {

    @Autowired
    JwtTokenProvider tokenProvider;

    @Scheduled(fixedRate = 3600000)
    public void purgeBlacklisted() {
        SecUserRepository.blackListedToken.forEach((token, timestamp) -> {
            if (!tokenProvider.validateToken(token))
                SecUserRepository.blackListedToken.remove(token);
        });

    }
}
