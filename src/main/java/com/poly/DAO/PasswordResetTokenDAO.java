package com.poly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Account;
import com.poly.model.PasswordResetToken;

public interface PasswordResetTokenDAO extends JpaRepository<PasswordResetToken, Long> {

    // Tìm kiếm token theo giá trị của nó
    PasswordResetToken findByToken(String token);

    // Kiểm tra xem token đã tồn tại chưa
    boolean existsByToken(String token);

	PasswordResetToken findByAccount(Account user);
}
