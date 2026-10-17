package com.simupi.repository;

import com.simupi.model.LoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for LoginAudit tracking.
 */
@Repository
public interface LoginAuditRepository extends JpaRepository<LoginAudit, Long> {

    List<LoginAudit> findByMobileOrderByLoginTimeDesc(String mobile);

    List<LoginAudit> findTop20ByOrderByLoginTimeDesc();
}
