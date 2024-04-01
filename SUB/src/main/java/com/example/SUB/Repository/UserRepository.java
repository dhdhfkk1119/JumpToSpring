package com.example.SUB.Repository;

import com.example.SUB.Entity.SiteUser;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SiteUser,Long> {

    Optional<SiteUser> findByusername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
