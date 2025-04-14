package com.poly.model;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class randomtoken {
	
	private static final SecureRandom secureRandom = new SecureRandom();
	
	private String generateVerificationCode() {
	    int code = secureRandom.nextInt(999999); // Số ngẫu nhiên từ 0 đến 999999
	    return String.format("%06d", code); // Chuyển thành chuỗi gồm 6 chữ số
	}
}
