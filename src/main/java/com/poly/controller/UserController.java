package com.poly.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.TicketDAO;
import com.poly.entity.Account;
import com.poly.entity.Ticket;
import com.poly.service.HomeService;

@Controller
public class UserController {
	@Autowired
	private AccountDAO accountDAO;

	@Autowired
	HomeService service;
	@Autowired
	private TicketDAO ticketDAO;

	// Thư mục lưu trữ ảnh avatar
	private final String UPLOAD_DIR = "src/main/resources/static/images/avatar/";

	@GetMapping("/profile")
	public String getUserProfile(Model model,
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("username") String username,
			RedirectAttributes attributes) {
		Optional<Account> accountOptional = accountDAO.findByEmail(username);

		if (accountOptional.isPresent()) {
			Account account = accountOptional.get();
			attributes.addFlashAttribute("file", account.getAvatar());
			model.addAttribute("accounts", account);
			return "/views/profile/profile"; // Đảm bảo đúng đường dẫn đến template
		} else {
			model.addAttribute("error", "Không tìm thấy tài khoản với username: " + username);
			return "/views/profile/error"; // Đảm bảo đúng đường dẫn đến template
		}
	}

	@PostMapping("/profile")
	public String save(@ModelAttribute("accounts") Account accounts, Model model,
			@RequestParam("file") MultipartFile attach) {
		if (!attach.getContentType().startsWith("image/")) {
			accountDAO.save(accounts);
		} else {
			String filename = attach.getOriginalFilename();
			accounts.setAvatar(filename);
			service.saveImage(attach);
			accountDAO.save(accounts);
		}

		return "/views/profile/profile";
	}

	@GetMapping("/profile/history")
	public String getHistory(Model model,
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "email", required = false) String email) {
		if (email == null || email.isEmpty()) {
			model.addAttribute("error", "Vui lòng nhập email để xem lịch sử mua vé.");
			return "/views/profile/buy";
		}

		Optional<Account> accountOptional = accountDAO.findByEmail(email);
		if (accountOptional.isPresent()) {
			Account account = accountOptional.get();
			List<Ticket> tickets = ticketDAO.findByAccount(account); // Giả định phương thức này đã có
			model.addAttribute("accounts", account);
			model.addAttribute("tickets", tickets);
			return "/views/profile/buy";
		} else {
			model.addAttribute("error", "Không tìm thấy tài khoản với email: " + email);
			return "/views/profile/buy";
		}
	}

	@PostMapping("/profile/history")
	public String postTicketHistory(@ModelAttribute("ticket") Ticket ticket, @RequestParam("email") String email,
			Model model) {
		if (email == null || email.isEmpty()) {
			model.addAttribute("error", "Email là bắt buộc để lưu thông tin vé.");
			return "/views/profile/buy";
		}

		if (ticket == null || ticket.getSomeRequiredField() == null) {
			model.addAttribute("error", "Thông tin vé không hợp lệ.");
			return "/views/profile/buy";
		}

		ticketDAO.save(ticket);
		return "redirect:/profile/history?email=" + email;
	}

	
}
