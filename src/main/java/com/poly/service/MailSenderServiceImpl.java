package com.poly.service;

import com.poly.DTO.AccountDTO;
import com.poly.model.MailInfo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(MailSenderServiceImpl.class);

    @Override
    public void sendEmail(MailInfo info) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Đặt mã hóa UTF-8
            
            helper.setTo(info.getTo());
            helper.setSubject(info.getSubject());
            helper.setText(info.getBody(), true); // 'true' để gửi email HTML
            
            mailSender.send(message);
            logger.info("Email sent successfully to {}", info.getTo());
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", info.getTo(), e.getMessage());
            throw e;
        }
    }

    @Override
    public void queue(MailInfo mail) {
        logger.info("Email queued for {}", mail.getTo());
        // Logic để đưa email vào hàng đợi (nếu cần)
    }

    @Override
    public void queue(String to, String subject, String body) {
        MailInfo mailInfo = new MailInfo(to, subject, body);
        queue(mailInfo);
    }
// gamil chào mừng
    @Override
    public void sendHtmlMail1(String name, String email) throws MessagingException {
    	String subject = "Chào mừng đến với dịch vụ của chúng tôi";
    	String body = "<html>" +
    	              "<head>" +
    	              "<style>" +
    	              "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
    	              ".container { background-color: #ffffff; padding: 20px; border-radius: 8px; max-width: 500px; margin: 0 auto; box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1); }" +
    	              "h1 { color: #4CAF50; font-size: 24px; text-align: center; }" +
    	              "p { font-size: 16px; color: #333333; line-height: 1.5; }" +
    	              ".welcome-text { font-size: 18px; color: #555; text-align: center; margin-top: 20px; }" +
    	              ".signature { margin-top: 30px; font-style: italic; color: #666; text-align: center; }" +
    	              "</style>" +
    	              "</head>" +
    	              "<body>" +
    	              "<div class='container'>" +
    	              "<h1>Chào mừng, " + name + "!</h1>" +
    	              "<p class='welcome-text'>Cảm ơn bạn đã đăng ký với chúng tôi! Chúng tôi rất vui mừng được đồng hành cùng bạn.</p>" +
    	              "<p class='signature'>Trân trọng,<br>Đội ngũ hỗ trợ</p>" +
    	              "</div>" +
    	              "</body>" +
    	              "</html>";

    	sendEmail(new MailInfo(email, subject, body));

    }


 // gmail Đặt lại mật khẩu
    @Override
    public void sendPasswordResetEmail(String email, String token) throws MessagingException {
    	String subject = "Đặt lại mật khẩu";
    	String body = "<html>" +
    	              "<head>" +
    	              "<style>" +
    	              "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
    	              ".container { background-color: #fff; border-radius: 8px; padding: 20px; max-width: 400px; margin: 0 auto; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }" +
    	              ".header { text-align: center; color: #4CAF50; font-size: 24px; margin-bottom: 10px; }" +
    	              ".message { font-size: 16px; color: #333; margin-bottom: 20px; }" +
    	              ".token { font-size: 18px; color: #FF6347; font-weight: bold; text-align: center; padding: 10px; border-radius: 4px; background-color: #f0f0f0; }" +
    	              ".footer { margin-top: 20px; text-align: center; font-size: 0.9em; color: #777; }" +
    	              "</style>" +
    	              "</head>" +
    	              "<body>" +
    	              "<div class='container'>" +
    	              "<h2 class='header'>Đặt lại mật khẩu</h2>" +
    	              "<p class='message'>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình. Vui lòng sử dụng mã xác thực dưới đây để hoàn tất quá trình:</p>" +
    	              "<div class='token'>" + token + "</div>" +
    	              "<p class='message'>Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này hoặc liên hệ với bộ phận hỗ trợ.</p>" +
    	              "</div>" +
    	              "<div class='footer'>" +
    	              "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
    	              "</div>" +
    	              "</body>" +
    	              "</html>";

    	// Gửi email
    	sendEmail(new MailInfo(email, subject, body));

    }
    
    // veri gmail
    @Override
    public void sendVerificationEmail(String email, String verificationCode) throws MessagingException {
    	String subjectvr1 = "Xác thực tài khoản";
    	String bodyvr1 = "<html>" +
    	                 "<head>" +
    	                 "<style>" +
    	                 "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
    	                 ".container { background-color: #ffffff; padding: 20px; border-radius: 8px; max-width: 400px; margin: 0 auto; box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1); }" +
    	                 "h2 { color: #4CAF50; font-size: 24px; margin-bottom: 20px; }" +
    	                 "p { font-size: 16px; color: #333333; line-height: 1.5; }" +
    	                 ".code { font-size: 18px; color: #d9534f; font-weight: bold; padding: 10px; background-color: #f9f9f9; border-radius: 5px; display: inline-block; }" +
    	                 "</style>" +
    	                 "</head>" +
    	                 "<body>" +
    	                 "<div class='container'>" +
    	                 "<h2>Xác Thực Tài Khoản</h2>" +
    	                 "<p>Chào bạn,</p>" +
    	                 "<p>Mã xác thực của bạn là: <span class='code'>" + verificationCode + "</span></p>" +
    	                 "<p>Vui lòng nhập mã này để xác thực tài khoản của bạn.</p>" +
    	                 "<p>Trân trọng,</p>" +
    	                 "<p>Đội ngũ hỗ trợ</p>" +
    	                 "</div>" +
    	                 "</body>" +
    	                 "</html>";

    	sendEmail(new MailInfo(email, subjectvr1, bodyvr1));

    }

	@Override
	public void sendVerificationEmail(String subjectvr, String bodyvr, String verificationCode)
			throws MessagingException {
		
	}

	@Override
	public void sendHtmlMail(@Valid String string, String email) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void senEmailConfirm(String email,String subject, String body) throws MessagingException {
		 MimeMessage message = mailSender.createMimeMessage();
         MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Đặt mã hóa UTF-8
         
         helper.setTo(email);
         helper.setSubject(subject);
         helper.setText(body,true); // 'true' để gửi email HTML
         
         mailSender.send(message);
		
	}

	@Override
	public void sendHtmlMail(String formEmail, String email,String customer,String phonenumber, String body) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Đặt mã hóa UTF-8
        helper.setFrom(formEmail);
        helper.setTo(email);
        helper.setSubject("Hỗ trợ : "+customer);
        helper.setText("Email liên hệ: "+email+"<br>"+
        				"Số điện thoại liên hệ: "+phonenumber+"<br>"+
        				body,true); // 'true' để gửi email HTML
        
        mailSender.send(message);
	}

	@Override
	public void handleCustomerContact(String customerName, String customerEmail, String phoneNumber, String messageBody) throws MessagingException {
	    // Gửi email đến bộ phận hỗ trợ
	    sendToSupport(customerName, customerEmail, phoneNumber, messageBody);

	    // Gửi email xác nhận cho khách hàng
	    sendConfirmationToCustomer(customerName, customerEmail);
	}

	// Gửi email đến bộ phận hỗ trợ
	private void sendToSupport(String customerName, String customerEmail, String phoneNumber, String messageBody) throws MessagingException {
	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	    helper.setFrom("datxe5ea@gmail.com"); // Email hệ thống
	    helper.setTo("datxe5ea@gmail.com"); // Email bộ phận hỗ trợ
	    helper.setSubject("Yêu cầu hỗ trợ từ khách hàng: " + customerName);

	    String emailContent = String.format(
	        """
	        <p><strong>Thông tin khách hàng:</strong></p>
	        <p>Tên: %s</p>
	        <p>Email: %s</p>
	        <p>Số điện thoại: %s</p>
	        <p><strong>Nội dung liên hệ:</strong></p>
	        <p>%s</p>
	        """, customerName, customerEmail, phoneNumber, messageBody
	    );

	    helper.setText(emailContent, true);
	    mailSender.send(message);
	}

	// Gửi email xác nhận cho khách hàng
	private void sendConfirmationToCustomer(String customerName, String customerEmail) throws MessagingException {
	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	    helper.setFrom("datxe5ea@gmail.com"); // Email hệ thống
	    helper.setTo(customerEmail); // Email của khách hàng
	    helper.setSubject("Xác nhận yêu cầu hỗ trợ");

	    String emailContent = String.format(
	        """
	        <p>Chào %s,</p>
	        <p>Cảm ơn bạn đã liên hệ với chúng tôi. Yêu cầu của bạn đã được tiếp nhận và sẽ được xử lý sớm nhất.</p>
	        <p>Nếu có thêm bất kỳ thông tin nào, vui lòng trả lời email này hoặc gọi tới số hotline của chúng tôi.</p>
	        <p>Trân trọng,</p>
	        <p>Đội ngũ hỗ trợ</p>
	        """, customerName
	    );

	    helper.setText(emailContent, true);
	    mailSender.send(message);
	}



	
}
