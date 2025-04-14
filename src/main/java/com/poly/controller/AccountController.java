package com.poly.controller;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.LocationDAO;
import com.poly.DAO.PasswordResetTokenDAO;
import com.poly.DAO.VerificationTokenDAO;
import com.poly.DTO.AccountDTO;
import com.poly.entity.Account;
import com.poly.entity.Route;
import com.poly.mapper.AccountMapper;
import com.poly.model.PasswordResetToken;
import com.poly.model.VerificationToken;
import com.poly.service.MailSenderService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class AccountController {
	
	private static final SecureRandom secureRandom = new SecureRandom();
	
	@Autowired
	private HttpServletRequest req;
	
	@Autowired
	private HttpServletResponse response;
	@Autowired
	private AccountDAO accountDAO;
	
	@Autowired
	private LocationDAO locationDAO ;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailSenderService mailSenderService;

	@Autowired
	private PasswordResetTokenDAO passwordResetTokenRepository;

	@Autowired
	private VerificationTokenDAO verificationTokenRepository;

	private String generateVerificationCode() {
		int code = secureRandom.nextInt(999999); // Số ngẫu nhiên từ 0 đến 999999
		return String.format("%06d", code); // Chuyển thành chuỗi gồm 6 chữ số
	}

	@GetMapping("/login")
	public String getLogin(Model model) {
		Account ac = new Account();
		model.addAttribute("account", ac);
		return "/views/home_items/Login";
	}

	@GetMapping("/registration")
	public String getRegistration(Model model) {
		AccountDTO ac = new AccountDTO();
		model.addAttribute("account", ac);
		return "/views/home_items/registration";
	}

	@PostMapping("/registration")
	public String registerAccount(RedirectAttributes redirectAttributes, Model model,
	        @Valid @ModelAttribute("account") AccountDTO accountDTO, BindingResult result) throws MessagingException {

	    // Kiểm tra mật khẩu và xác nhận mật khẩu
	    if (!accountDTO.getPassword().equals(accountDTO.getConfirmpassword())) {
	        result.addError(new FieldError("account", "confirmpassword", "Mật khẩu không khớp"));
	    }
	    // Kiểm tra xem username đã tồn tại chưa
	    if (accountDAO.existsByUsername(accountDTO.getUsername())) {
	        result.addError(new FieldError("account", "username", "Tên người dùng đã tồn tại"));
	    }


	    // Kiểm tra xem email đã tồn tại chưa
	    if (accountDAO.existsByEmail(accountDTO.getEmail())) {
	        Account existingAccount = accountDAO.findByEmail(accountDTO.getEmail()).get();
	        existingAccount.setUsername(accountDTO.getUsername());
	        existingAccount.setFullname(accountDTO.getFullname());
	        existingAccount.setPhone(accountDTO.getPhone());

	        // Nếu email đã tồn tại và tài khoản đã được kích hoạt
	        if (existingAccount.getIsEnabled()) {
	            result.addError(new FieldError("account", "email", "Email đã được sử dụng"));
	        } 
	        else {
	            // Email đã tồn tại nhưng tài khoản chưa được kích hoạt
	            // Tạo token xác thực mới và gửi email
	            String verificationCode = generateVerificationCode();
	            VerificationToken token = new VerificationToken(verificationCode, existingAccount);
	            verificationTokenRepository.save(token);

	            mailSenderService.sendVerificationEmail(existingAccount.getEmail(), verificationCode);

	            // Thông báo cho người dùng rằng email này đã được đăng ký và đang chờ xác thực
	            model.addAttribute("message", "Email này đã được đăng ký. Vui lòng kiểm tra email để xác thực tài khoản.");
	            return "redirect:/verify-account"; 
	        }
	    }

	    // Nếu có lỗi, quay lại trang đăng ký
	    if (result.hasErrors()) {
	        return "/views/home_items/registration";
	    }

	    // Mã hóa mật khẩu và lưu tài khoản chưa kích hoạt
	    String encodedPassword = passwordEncoder.encode(accountDTO.getPassword());
	    accountDTO.setPassword(encodedPassword);

	    // Chuyển đổi AccountDTO sang Account entity
	    Account account = AccountMapper.toAccount(accountDTO, encodedPassword);

	    accountDAO.save(account);

	    // Tạo mã xác thực và token
	    String verificationCode = generateVerificationCode();
	    VerificationToken token = new VerificationToken(verificationCode, account);

	    // Lưu mã xác thực vào cơ sở dữ liệu
	    verificationTokenRepository.save(token);

	    // Gửi email với mã xác nhận
	    mailSenderService.sendVerificationEmail(account.getEmail(), verificationCode);

	    // Chuyển hướng đến trang thông báo 
	    model.addAttribute("message", "Email xác thực đã được gửi đến " + account.getEmail() + ".");
	    return "redirect:/verify-account";
	}

	@GetMapping("/forget")
	public String forgetPasswordForm(Model model, RedirectAttributes redirectAttributes,
			@ModelAttribute Account account) {
		redirectAttributes.addFlashAttribute("account", account);
		return "views/home_items/forget";
	}

	@PostMapping("/forget")
	public String forgotPassword(@ModelAttribute Account account, RedirectAttributes redirectAttributes, Model model)
			throws MessagingException {
		String email = account.getEmail(); // Get email from the Account object
		Optional<Account> userOptional = accountDAO.findByEmail(email);

		if (userOptional.isEmpty()) {
			model.addAttribute("emailError", "Email không tồn tại trong hệ thống.");
			return "views/home_items/forget"; // Return to the form with error message
		}
	    Account user = userOptional.get();

		PasswordResetToken existingToken = passwordResetTokenRepository.findByAccount(user);
		 if (existingToken != null) {
		        // Kiểm tra token đã hết hạn
		        if (existingToken.isExpired()) {
		            passwordResetTokenRepository.delete(existingToken); // Xóa token đã hết hạn
		        } else {
		        	redirectAttributes.addFlashAttribute("token", existingToken.getToken());
		        	return "redirect:/ResetPassword"; // Chuyển đến trang đặt lại mật khẩu
		        }
		    }
		 String token = generateUniqueToken();
		 PasswordResetToken resetToken = new PasswordResetToken(token, user);
		try {
			passwordResetTokenRepository.save(resetToken);
		} catch (DataIntegrityViolationException e) {
			model.addAttribute("error", "Có lỗi xảy ra, vui lòng thử lại.");
			return "views/home_items/forget"; // Trả về trang quên mật khẩu nếu xảy ra lỗi
		}

		// Gửi email với mã xác nhận
		mailSenderService.sendPasswordResetEmail(email, token);

		// Chuyển hướng đến trang thông báo rằng email đặt lại mật khẩu đã được gửi
		redirectAttributes.addFlashAttribute("account", userOptional.get());
		return "redirect:/ResetPassword";
	}

	private String generateUniqueToken() {
		String token;
		do {
			token = generateVerificationCode(); // Tạo mã 6 chữ số
		} while (passwordResetTokenRepository.existsByToken(token)); // Kiểm tra xem mã đã tồn tại trong DB chưa
		return token;
	}

	@GetMapping("/verify-account")
	public String getVerifyAccount(@RequestParam(required = false) String token, Model model) {
		model.addAttribute("token", token); // Thêm token vào model
		return "/views/home_items/VerifyAccount"; // Chuyển đến trang xác thực
	}

	@PostMapping("/verify-account")
	public String verifyAccount(@RequestParam String verificationCode, @RequestParam String token,
	        RedirectAttributes redirectAttributes, Model model) throws MessagingException {
	    // Tìm kiếm VerificationToken từ cơ sở dữ liệu
	    VerificationToken vrtoken = verificationTokenRepository.findByToken(verificationCode);

	    // Kiểm tra token hợp lệ và chưa hết hạn
	    if (vrtoken != null && !vrtoken.isExpired()) {
	    	if (verificationCode.equals(vrtoken.getToken())) {
	            // Lấy thông tin tài khoản từ VerificationToken
	            Account account = vrtoken.getAccount();

	            
	            // Xóa mã xác thực và tài khoản chưa xác thực
	            account.setIsEnabled(true);
	            accountDAO.save(account);  // Lưu tài khoản vào cơ sở dữ liệu
	            verificationTokenRepository.delete(vrtoken);  // Xóa mã xác thực đã sử dụng	  
	            // Gửi email chào mừng
	            System.out.println("Đang gửi email chào mừng đến: " + account.getEmail());
	            mailSenderService.sendHtmlMail1(account.getFullname(), account.getEmail());  // Gửi email

	            // Thêm thông báo thành công
	            redirectAttributes.addFlashAttribute("message", "Tài khoản của bạn đã được xác thực thành công.");

	            // Chuyển hướng đến trang đăng nhập
	            return "redirect:/login";  // Chuyển đến trang đăng nhập
	        } 
	    	else {
	    		model.addAttribute("error", "Mã đã hết hạn hoặc không hợp lệ.");
			}
	    }
	    model.addAttribute("error", "Mã đã hết hạn hoặc không hợp lệ. Vui lòng nhập mã xác thực cũ để lấy mã mới");
	    return "/views/home_items/VerifyAccount";  // Trả về trang lỗi xác thực
	}

	@PostMapping("/resend-verification-code")
	public String resendVerificationCode(@RequestParam String token, RedirectAttributes redirectAttributes){
	    // Tìm kiếm VerificationToken từ cơ sở dữ liệu
	    
	    try {
	    	VerificationToken vrtoken = verificationTokenRepository.findByToken(token);
	    	if (vrtoken != null && vrtoken.isExpired()) {
		        // Lấy tài khoản liên quan
		        Account account = vrtoken.getAccount();
		       
		        // Tạo mã xác thực mới
		        String newCode = generateVerificationCode();
		        verificationTokenRepository.delete(vrtoken);
		        VerificationToken newtoken = new VerificationToken(newCode, account);
		        verificationTokenRepository.save(newtoken);

		        // Gửi email xác thực mới
		        mailSenderService.sendVerificationEmail(account.getEmail(), newCode);

		        // Thêm thông báo thành công
		        redirectAttributes.addFlashAttribute("message", "Mã xác thực mới đã được gửi tới email của bạn.");
		    } else {
		    	
		        redirectAttributes.addFlashAttribute("error", "Không thể gửi lại mã xác thực. Vui lòng nhập mã xác thực để lấy mã mới");
		    }

		    // Chuyển hướng lại trang xác thực
		   
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	    return "redirect:/verify-account";
	    
	}

	@GetMapping("/ResetPassword")
	public String showResetPasswordForm(Model model) {
		return "/views/home_items/ResetPassword";
	}

	@PostMapping("/ResetPassword")
	public String handleResetPassword(
			@RequestParam String verificationCode,
	        RedirectAttributes redirectAttributes, Model model,
			@RequestParam String newPassword) {
		// Kết hợp OTP
		String otp = verificationCode;

		// Kiểm tra mã OTP (có thể so với dữ liệu trong database)
		PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(otp);
		if (resetToken == null || resetToken.isExpired()) {
			redirectAttributes.addFlashAttribute("error", "Token không hợp lệ hoặc đã hết hạn. Vui Lòng nhập mã xác nhận để lấy mã mới");
			return "redirect:/ResetPassword"; // Quay lại form nếu token không hợp lệ
		}

		// Lấy người dùng từ token
		Account account = resetToken.getAccount();

		// Cập nhật mật khẩu mới
		account.setPassword(passwordEncoder.encode(newPassword));
		accountDAO.save(account);

		// Vô hiệu hóa token sau khi sử dụng
		passwordResetTokenRepository.delete(resetToken);

		redirectAttributes.addFlashAttribute("message", "Mật khẩu đã được đặt lại thành công.");
		return "redirect:/login"; // Chuyển đến trang đăng nhập
	}
	
	@PostMapping("/resend-forget-code")
	public String resendForgetCode(@RequestParam String token, RedirectAttributes redirectAttributes){
	    // Tìm kiếm VerificationToken từ cơ sở dữ liệu
	    
	    try {
	    	PasswordResetToken vrtoken = passwordResetTokenRepository.findByToken(token);
	    	if (vrtoken != null && vrtoken.isExpired()) {
		        // Lấy tài khoản liên quan
		        Account account = vrtoken.getAccount();
		       
		        // Tạo mã xác thực mới
		        String newCode = generateVerificationCode();
		        passwordResetTokenRepository.delete(vrtoken);
		        PasswordResetToken newtoken = new PasswordResetToken(newCode, account);
		        passwordResetTokenRepository.save(newtoken);

		        // Gửi email xác thực mới
		        mailSenderService.sendVerificationEmail(account.getEmail(), newCode);

		        // Thêm thông báo thành công
		        redirectAttributes.addFlashAttribute("message", "Mã xác thực mới đã được gửi tới email của bạn.");
		    } else {
		    	
		        redirectAttributes.addFlashAttribute("error", "Không thể gửi lại mã xác thực. Vui lòng nhập mã xác thực để lấy mã mới");
		    }

		    // Chuyển hướng lại trang xác thực
		   
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	    return "redirect:/ResetPassword";
	    
	}
	
		//------QUẢN LÝ ACCOUNT-------///
    @GetMapping("/manager/account")
    public String getRoute(
    		Model model,
    		@ModelAttribute("account") Account attributes,
    		//annotation để lấy thông tin về người dùng hiện tại đã được xác thực từ Security Context 
    		@AuthenticationPrincipal UserDetails userDetails) {
    	
		String email = userDetails.getUsername();
		Account accountfind = accountDAO.findByEmail(email).get();
		model.addAttribute("username", accountfind.getUsername());
		
		//xét giá trị attributes nếu ko null thì lấy attributes ko thì new attributes
        Account account = (attributes != null) ? attributes : new Account();
        model.addAttribute("account", account);

        List<Account> accountList = accountDAO.findAll();
        model.addAttribute("accountList", accountList);

        return "/views/items/account";
    }

    @PostMapping("/manager/account")
    public String save(@ModelAttribute("account") Account account, RedirectAttributes redirectAttributes) {
        try {
             accountDAO.save(account); // Lưu dữ liệu
             redirectAttributes.addFlashAttribute("successMessage", "Tài khoản được lưu thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi lưu tài khoản: " + e.getMessage());
        }
        return "redirect:/manager/account";
    }


    @GetMapping("/manager/account/edit/{id}")
    public String editForm(@PathVariable Integer id,Model model, @ModelAttribute("account") Account attributes) {
    	 Account account = accountDAO.findById(id).orElse(null);
    	 model.addAttribute("account", account);
    	 return "/views/account_items/manager";
    }
    @PostMapping("/manager/account/edit")
    public String edit(@ModelAttribute Account account,
    		RedirectAttributes attributes) {
        accountDAO.save(account);
        attributes.addFlashAttribute("successMessage", "Cập nhật tài khoản thành công.");
        return "redirect:/manager/account";
    }



    @PostMapping("/manager/account/reset")
    public String resetAccountForm(
    		Model model,
    		RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("account", new Account());
        return "redirect:/manager/account/edit";
    }

    @PostMapping("/manager/account/remove")
    public String getRemove(@ModelAttribute("account") Account account,
                            @AuthenticationPrincipal UserDetails userDetails,
                            HttpServletRequest req, HttpServletResponse response,
                            Model model, RedirectAttributes redirectAttributes) {
        try {
            // Lấy tên tài khoản đang đăng nhập
            String loggedInUsername = userDetails.getUsername();

            // Kiểm tra nếu tài khoản cần xóa là tài khoản đang đăng nhập
            if (account.getEmail().equals(loggedInUsername)) {
                accountDAO.delete(account); // Xóa tài khoản

                // Hủy phiên làm việc và chuyển hướng về trang đăng nhập
                req.getSession().invalidate();
                response.sendRedirect("/home");
                return null; // Ngừng thực hiện tiếp
            }

            // Xóa tài khoản khác
            accountDAO.delete(account);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tài khoản thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa tài khoản: " + e.getMessage());
            model.addAttribute("route", new Route());
            model.addAttribute("locationArray", locationDAO.findAll());
            return "home";
        }
        return "redirect:/manager/account";
    }

}
