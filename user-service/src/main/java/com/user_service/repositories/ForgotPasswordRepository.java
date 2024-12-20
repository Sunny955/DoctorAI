package com.user_service.repositories;

import com.user_service.entity.ForgotPassword;
import com.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    Optional<ForgotPassword> findByOtp(Integer otp);
    Optional<ForgotPassword> findByUser(User user);
}
