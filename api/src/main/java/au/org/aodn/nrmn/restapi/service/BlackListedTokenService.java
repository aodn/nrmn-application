package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.repository.SecUserEntityRepository;
import au.org.aodn.nrmn.restapi.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BlackListedTokenService {

    @Autowired
    JwtTokenProvider tokenProvider;

    @Scheduled(fixedRate = 43200000)
    public void purgeBlacklisted() {
        SecUserEntityRepository.blackListedToken.forEach((key, token) -> {
            if (!tokenProvider.validateToken(token))
                SecUserEntityRepository.blackListedToken.remove(key);
        });

    }
}
