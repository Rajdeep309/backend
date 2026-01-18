package com.crypto.PortfolioTracker.Repository;

import com.crypto.PortfolioTracker.Model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    interface UserDetailsProjection {
        Long getId();
        String getName();
        String getPassword();
    }
    Optional<UserDetailsProjection> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.email = :email")
    void resetPassword(String email, String newPassword);
}
