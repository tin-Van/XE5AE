package com.poly.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.BusDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.TicketDAO;
import com.poly.DAO.TripDAO;
import com.poly.entity.Account;
import com.poly.entity.Ticket;


@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    AccountDAO accountDAO;
    
    @Autowired
    BusDAO busDAO;
    
    @Autowired
    RouteDAO routeDAO;
    
    @Autowired
    TicketDAO ticketDAO;
    
    @Autowired
    TripDAO tripDAO;
    
    @GetMapping("")
    public String Manager(Model model,
    		//annotation để lấy thông tin về người dùng hiện tại đã được xác thực từ Security Context
    		@AuthenticationPrincipal UserDetails userDetails) {
    	
    	String email = userDetails.getUsername();
    	Account account = accountDAO.findByEmail(email).get();
    	model.addAttribute("username", account.getUsername());
    	
        Long bus = busDAO.countTotalBuses();
        model.addAttribute("totalbus", bus);

        Long trip = tripDAO.countTotalTrip();
        model.addAttribute("totalroute", trip);

        Long accountTotal = accountDAO.countTotalAccount();
        model.addAttribute("totalaccount", accountTotal);

        List<Account> accTop4 = accountDAO.findTop4ByOrderByCreatedDateDesc();
        model.addAttribute("accnew", accTop4);

        List<Ticket> top4tknew = ticketDAO.findTop4ByOrderByPurchaseTimeDesc();
        model.addAttribute("top4tknew", top4tknew);

        Long ticket = ticketDAO.countTotalTickets();
        model.addAttribute("totalticket", ticket);

        return "/views/items/manager";
    }
}