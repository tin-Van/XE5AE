package com.poly.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "fullname", columnDefinition = "nvarchar(255)")
    @NotEmpty(message = "Vui điền thông tin đầy đủ")
    private String fullname;

    @NotEmpty(message = "Vui điền thông tin đầy đủ")
    private String username;
    
    @NotEmpty(message = "Vui điền thông tin đầy đủ")
    private String password;

    @NotEmpty(message = "Vui điền thông tin đầy đủ")
    private String confirmpassword;
    
    @NotNull(message = "Vui điền thông tin đầy đủ")
    private Boolean gender;

    @NotEmpty(message = "Vui điền thông tin đầy đủ")
    private String email;

    @NotEmpty(message = "Vui điền thông tin đầy đủ")
    private String phone;

    @Column(name = "role")
    private String role;
    
    @Column(name = "avatar")
    private String avatar;
}
