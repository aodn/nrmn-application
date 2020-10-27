package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.SecUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public interface SecUserRepository extends JpaRepository<SecUser, Long>, JpaSpecificationExecutor<SecUser> {
    Optional<SecUser> findByEmail(String Email);

    ConcurrentHashMap<String, Long> blackListedToken = new ConcurrentHashMap<>();

    static Optional<Long> addBlackListedToken(Long timestamp, String token) {
        return Optional.ofNullable(blackListedToken.putIfAbsent(token, timestamp));
    }

    static boolean blackListedTokenPresent(String token) {
        return blackListedToken.getOrDefault(token, -1L) != -1L;
    }

}
