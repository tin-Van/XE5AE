package com.poly.entity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poly.model.PasswordResetToken;
import com.poly.model.VerificationToken;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fullname", columnDefinition = "nvarchar(255)")
    @NotEmpty(message = "Vui lòng điền thông tin đầy đủ")
    private String fullname;

    @Column(name = "username", unique = true,columnDefinition = "nvarchar(255)")
    @NotEmpty(message = "Vui lòng điền thông tin đầy đủ")
    private String username;

    @Column(name = "password",columnDefinition = "nvarchar(255)")
    @JsonIgnore // Ẩn giá trị mật khẩu khi trả về dữ liệu từ API
    @NotEmpty(message = "Vui lòng điền thông tin đầy đủ")
    private String password;

    @Column(name = "gender")
    @NotNull(message = "Vui lòng điền thông tin đầy đủ")
    private Boolean gender;

    @Column(name = "email", unique = true,columnDefinition = "nvarchar(255)")
    @NotEmpty(message = "Vui lòng điền thông tin đầy đủ")
    private String email;

    @Column(name = "phone")
    @NotEmpty(message = "Vui lòng điền thông tin đầy đủ")
    private String phone;

    private Boolean isEnabled = false;;
    
    @Column(name = "role")
    @NotEmpty
    private String role;

    @Column(name = "avatar",columnDefinition = "nvarchar(255)")
    private String avatar;
    
    @Column(name = "created_Date", updatable = false)
    private Timestamp createdDate;
    
    @OneToMany(mappedBy = "account",orphanRemoval = true)
    private List<Ticket> tickets;
    
    @OneToMany(mappedBy = "account",orphanRemoval = true)  // Mối quan hệ với PasswordResetToken
    private List<PasswordResetToken> passwordResetTokens;

    @OneToMany(mappedBy = "account",orphanRemoval = true)  // Mối quan hệ với VerificationToken
    private List<VerificationToken> verificationTokens;
    
	public void setToken(String verificationCode) {
		// TODO Auto-generated method stub
		// Xử lý logic nếu cần
	}
	
    @PrePersist
    public void prePersist() {
        this.createdDate = new Timestamp(System.currentTimeMillis());
    }
}
