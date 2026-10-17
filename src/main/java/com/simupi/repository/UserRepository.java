package com.simupi.repository;

import com.simupi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository interface for User entity persistence.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByMobile(String mobile);

    Optional<User> findByEmail(String email);

    Optional<User> findByUpiId(String upiId);

    boolean existsByMobile(String mobile);

    boolean existsByEmail(String email);

    boolean existsByUpiId(String upiId);
}
