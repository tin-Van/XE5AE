package com.poly.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.SeatDAO;
import com.poly.DAO.TicketDAO;
import com.poly.entity.Account;
import com.poly.entity.Report;
import com.poly.entity.Seat;
import com.poly.entity.Ticket;
/*import com.poly.service.ReportService;*/
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/manager")
public class TicketController {

	@Autowired
	TicketDAO dao;

	@Autowired
	SeatDAO seatDAO;

	@Autowired
	AccountDAO accountDAO;

	@GetMapping("ticket")
	public String inventory(Model model, @AuthenticationPrincipal UserDetails userDetails) {

		String email = userDetails.getUsername();
		Account account = accountDAO.findByEmail(email).get();
		model.addAttribute("username", account.getUsername());
		
		List<Ticket> items = dao.findAll();
		List<Seat> seats = seatDAO.findAll();
		model.addAttribute("seats", seats);
		model.addAttribute("tickets", items);
		return "/views/items/ticket";
	}

	@PostMapping("ticket")
	public String postMethodName(@RequestParam String paystatus, @RequestParam String seatID, @RequestParam String id) {
		Optional<Ticket> ticketOptional = dao.findById(Integer.valueOf(id));
		Ticket ticket = ticketOptional.get();
		Optional<Seat> seatOptional = seatDAO.findById(Integer.valueOf(seatID));
		Seat seat = seatOptional.get();
		ticket.setPaymentstatus(paystatus); 
		
		if (paystatus.equals("True")) {
			seat.setBooked(true);
		} else {
			seat.setBooked(false);
		}
		seatDAO.save(seat);
		dao.save(ticket);
		System.out.print(paystatus);
		return "redirect:/manager/ticket";
	}
	

	

}
