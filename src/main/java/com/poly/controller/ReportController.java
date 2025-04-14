package com.poly.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.internal.TransactionManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.poly.DAO.AccountDAO;
import com.poly.DAO.BusDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.TicketDAO;
import com.poly.DAO.TripDAO;
import com.poly.entity.Account;
import com.poly.entity.Bus;
import com.poly.entity.Report;
//import com.poly.service.ReportService;
import com.poly.service.ReportService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/manager")
public class ReportController {

	@Autowired
	ReportService service;

	@Autowired
	BusDAO busDAO;

	@Autowired
	RouteDAO routeDAO;

	@Autowired
	TripDAO tripDAO;
	
	@Autowired
	TicketDAO ticketDAO;

	@Autowired
	AccountDAO accountDAO;

	@Autowired
	HttpServletResponse response;

	@GetMapping("report")
	public String inventory(Model model, @AuthenticationPrincipal UserDetails userDetails) throws IOException {

		String email = userDetails.getUsername();
		Account account = accountDAO.findByEmail(email).get();
		model.addAttribute("username", account.getUsername());

		List<Report> routeRevenue = service.getInventoryByCategory();
		model.addAttribute("reports", routeRevenue);

		Long bus = busDAO.countTotalBuses();
		model.addAttribute("totalbus", bus);

        Long trip = tripDAO.countTotalTrip();
        model.addAttribute("totalroute", trip);
		
		Long ticket = ticketDAO.countTotalTickets();
		model.addAttribute("totalticket", ticket);

		Long Sumticket = ticketDAO.countTotalSum();
		model.addAttribute("totalsum", Sumticket);

		ObjectMapper objectMapper = new ObjectMapper();
		// Tạo Json doanh thu tuyến đường
		String jsonArray = objectMapper.writeValueAsString(routeRevenue);
		model.addAttribute("jsonArray", jsonArray);
		// Tạo Json doanh thu tháng
		int year = LocalDate.now().getYear();
		List<Report> list = service.findRevenueByMonthOfYear(year);
		String monthjson = objectMapper.writeValueAsString(list);
		model.addAttribute("monthjson", monthjson);
		model.addAttribute("year", year);
		return "/views/items/report";
	}

	@PostMapping("report")
	public String Postinventory(Model model, @RequestParam int year) throws IOException {

		List<Report> routeRevenue = service.getInventoryByCategory();
		model.addAttribute("reports", routeRevenue);

		Long bus = busDAO.countTotalBuses();
		model.addAttribute("totalbus", bus);

        Long trip = tripDAO.countTotalTrip();
        model.addAttribute("totalroute", trip);

		Long ticket = ticketDAO.countTotalTickets();
		model.addAttribute("totalticket", ticket);
//
		Long Sumticket = ticketDAO.countTotalSum();
		model.addAttribute("totalsum", Sumticket);

		ObjectMapper objectMapper = new ObjectMapper();
		// Tạo Json doanh thu tuyến đường
		String jsonArray = objectMapper.writeValueAsString(routeRevenue);
		model.addAttribute("jsonArray", jsonArray);
		// Tạo Json doanh thu tháng

		List<Report> list = service.findRevenueByMonthOfYear(year);
		String monthjson = objectMapper.writeValueAsString(list);
		model.addAttribute("year", year);
		model.addAttribute("monthjson", monthjson);
		System.out.print(monthjson);
		return "/views/items/report";
	}

}
