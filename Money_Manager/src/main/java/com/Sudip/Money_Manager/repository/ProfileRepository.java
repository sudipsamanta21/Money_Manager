package com.Sudip.Money_Manager.repository;
import com.Sudip.Money_Manager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    Optional<ProfileEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<ProfileEntity> findByActivationToken(String activationToken);
}
