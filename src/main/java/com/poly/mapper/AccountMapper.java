package com.poly.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.poly.DTO.AccountDTO;
import com.poly.entity.Account;


@Component
public class AccountMapper {
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public static Account toAccount(AccountDTO dto,String password) {
		Account account = new Account();
		account.setUsername(dto.getUsername());
		account.setFullname(dto.getFullname());
		account.setEmail(dto.getEmail());
		account.setGender(dto.getGender());
		account.setPhone(dto.getPhone());
		account.setPassword(password);
	    account.setRole("authenticated");
		return account;
		
	}
	
}
