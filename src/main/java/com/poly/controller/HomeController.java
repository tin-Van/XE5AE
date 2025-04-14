package com.poly.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.DAO.AccountDAO;
import com.poly.DAO.LocationDAO;
import com.poly.DAO.TicketDAO;
//import com.poly.DAO.ContenDAO;
//import com.poly.DAO.LocationDAO;
//import com.poly.DAO.TicketDAO;
import com.poly.entity.Location;
import com.poly.entity.Route;
import com.poly.entity.Ticket;
import com.poly.entity.Trip;
import com.poly.model.Order;
import com.poly.service.MailSenderService;
import com.poly.service.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("home")
public class HomeController {
	
	@Autowired
	ObjectMapper objectMapper; 
	
	@Autowired
	HttpSession session;
//	
	@Autowired
	service service;

	@Autowired
	LocationDAO locationDAO;
//
	@Autowired
	MailSenderService senderService;
//
	@Autowired
	TicketDAO ticketDAO;

	@Autowired
	AccountDAO accountDAO;

	@Autowired
	HttpServletRequest request;

	@GetMapping("")
	public String getMethodName(Model model, @ModelAttribute("order") Order attributes) throws JsonProcessingException {

		List<Object[]> MostBookedRoutes = ticketDAO.findMostBookedRoutes();
		model.addAttribute("MostBookedRoutes", MostBookedRoutes);
		model.addAttribute("route", new Route());

		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locationArray", locations);
		
		List<Location> locationArray = new ArrayList<>(locations);
		//chuyển đổi danh sách Location sang chuỗi JSON
		String jsonArray = objectMapper.writeValueAsString(locationArray);
		model.addAttribute("locations", jsonArray);
		
		return "Home";
	}

	@GetMapping("/lienhe")
	public String lienhe(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tin-noi-bat/lienhe";
	}
	
	@PostMapping("/lienhe")
	public String sendMessage(Model model,
			@RequestParam String customer,
			@RequestParam String email,
			@RequestParam String phonenumber,
			@RequestParam String message
			) throws MessagingException {
		//Gửi mail qua cho nhân viên hỗ trợ
		senderService.handleCustomerContact(customer,email,phonenumber,message);
		System.out.println("Có hổ trợ với : " + email);
		return "/views/tin-noi-bat/lienhe";
	}
	
	@GetMapping("/udnb")
	public String udnb(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tin-noi-bat/UDNB";
	}

	@GetMapping("/tracuu")
	public String tintuc(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("ticket", new Ticket());
		return "/views/tin-noi-bat/tracuu";
	}
	
	@PostMapping("/ticket/check")
	public String postCheck(@ModelAttribute Ticket ticket,Model model) {
		List<Ticket> tickets = ticketDAO.findByTicketCode(ticket.getTicketCode());
		if (tickets.isEmpty()) {
			model.addAttribute("message", "Không tìm thấy vé xe");
		}
		model.addAttribute("tickets", tickets);
		return "/views/tin-noi-bat/tracuu";
	}
	@GetMapping("/Uu-dai-tai-khoan")
	public String taikhoan(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tin-noi-bat/tai-khoan";
	}

	@GetMapping("/Uu-dai-dat-ve")
	public String datve(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tin-noi-bat/dat-ve";
	}

	@GetMapping("/Uu-dai-tuyen-duong")
	public String tuyenduong(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tin-noi-bat/tuyen-duong";
	}

	@GetMapping("/Uu-dai-tuyen-duong-2")
	public String tuyenduong2(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tin-noi-bat/tuyen-duong-2";
	}

	@GetMapping("/Uu-dai-tuyen-duong-3")
	public String tuyenduong3(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tin-noi-bat/tuyen-duong-3";
	}

	@GetMapping("/Tip-Ha-Noi")
	public String hanoi(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tip-du-lich/ha-noi";
	}

	@GetMapping("/Tip-Sai-Gon")
	public String saigon(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tip-du-lich/sai-gon";
	}

	@GetMapping("/Tip-Vung-Tau")
	public String vungtau(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tip-du-lich/vung-tau";
	}

	@GetMapping("/Tip-Da-Lat")
	public String dalat(Model model) {
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		model.addAttribute("route", new Route());
		return "/views/tip-du-lich/da-lat";
	}
}
