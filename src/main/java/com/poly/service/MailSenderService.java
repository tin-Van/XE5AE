package com.poly.service;

import com.poly.DTO.AccountDTO;
import com.poly.model.MailInfo;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface MailSenderService {

    // Gửi email thông thường
    void sendEmail(MailInfo info) throws MessagingException;

    // Đưa email vào hàng đợi
    void queue(MailInfo mail);

    // Đưa email vào hàng đợi với thông tin cơ bản
    void queue(String to, String subject, String body);

    // Gửi email HTML (ví dụ: email chào mừng)
    void sendHtmlMail(@Valid String string, String email) throws MessagingException;

    // Gửi email đặt lại mật khẩu
    void sendPasswordResetEmail(String email, String resetUrl) throws MessagingException;

    // Gửi email xác thực tài khoản
    void sendVerificationEmail( String subjectvr, String bodyvr) throws MessagingException;

	void sendVerificationEmail(String subjectvr, String bodyvr, String verificationCode) throws MessagingException;

	void senEmailConfirm(String email, String subject, String body)throws MessagingException;


	void sendHtmlMail(String formEmail, String email, String customer, String phonenumber, String body)
			throws MessagingException;

	void sendHtmlMail1(String name, String email) throws MessagingException;

	void handleCustomerContact(String customerName, String customerEmail, String phoneNumber, String messageBody)
			throws MessagingException;
	

	
}
