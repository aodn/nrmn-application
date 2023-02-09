package au.org.aodn.nrmn.restapi.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import au.org.aodn.nrmn.restapi.data.model.GlobalLock;

public interface GlobalLockRepository extends JpaRepository<GlobalLock, Long> {
}
