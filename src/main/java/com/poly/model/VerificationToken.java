package com.poly.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.poly.DTO.AccountDTO;
import com.poly.entity.Account;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String token; // Mã xác thực

    private LocalDateTime expiryDate; // Thời gian hết hạn
    
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    
    public VerificationToken(String token,Account account) {
        this.token = token;
        this.account = account;
        this.expiryDate = LocalDateTime.now().plusMinutes(1); // Token hết hạn sau 24 giờ
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}