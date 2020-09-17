package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SecUserRepository extends JpaRepository<SecUserEntity, Long> {

//    Optional<SecUserEntity> findByUsername(String username);
//
//    Optional<SecUserEntity> findByUsernameIgnoreCase(String username);

//    @Query("SELECT u FROM SecUserEntity u WHERE u.id = ?1 and u.status = 'ACTIVE'")
//    Optional<SecUserEntity> findByIdActive(Long id);

//  List<SecUserEntity> findByIdIn(List<Long> ids);

    Boolean existsByUsername(String username);

//    Page<SecUserEntity> findByUsername(Pageable pageable, String username);

//  Optional<SecUserEntity> findByEmailAddress(String emailAddress);

    Boolean existsByEmailAddress(String emailAddress);

//  Optional<SecUserEntity> findByUsernameOrEmailAddress(String username, String email);

//    @Query("select u from SecUserEntity u inner join u.roles ur where CAST(ur.name AS text)=:roleName")
//    Optional<SecUserEntity> findOneByRole(String roleName);
//
//    @Query("select u from SecUserEntity u inner join u.roles ur where CAST(ur.name AS text)=:roleName")
//    List<SecUserEntity> findAllByRole(String roleName);
    
//    @Query("select distinct u from SecUserEntity u")
//    Page<SecUserEntity> findAll(
//            Pageable pageable,
//            @Param("username") String username,
//            @Param("searchNamePattern") String searchNamePattern,
//            @Param("includeStatus") List<SecUserStatus> includeStatus,
//            @Param("excludeUsernames") List<String> excludeUsernames);
//
//    @Query("SELECT u FROM SecUserEntity u " +
//            "where (u.status = 'ACTIVE')" +
//            "and (u.username not in (:excludeUsernames))")
//    List<SecUserEntity> findAllActiveUsers(@Param("excludeUsernames") List<String> excludeUsernames);
}
