package com.poly.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.DAO.BusDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.SeatDAO;
import com.poly.DAO.TicketDAO;
import com.poly.DAO.TripDAO;
import com.poly.entity.Bus;
import com.poly.entity.Route;
import com.poly.entity.Seat;
import com.poly.entity.Ticket;
import com.poly.entity.Trip;

@Controller
public class SeatController {

	@Autowired
	SeatDAO seatDAO;

	@Autowired
	BusDAO busDAO;

	@Autowired
	RouteDAO routeDAO;

	@Autowired
	TripDAO tripDAO;
	
	@Autowired
	TicketDAO ticketDAO;
	
	@GetMapping("/trips/{tripId}/seats")
    public String getSeatsByRoute(@PathVariable int tripId, Model model) {
        Optional<Trip> tripOptional = tripDAO.findById(tripId);
        if (!tripOptional.isPresent()) {
            // Handle case where route is not found
            model.addAttribute("error", "Route not found");
            return "error";
        } 
        List<Seat> seats = seatDAO.findByTripId(tripId);
        Trip trip = tripOptional.get();
        model.addAttribute("trip", trip);
        model.addAttribute("seats", seats);
        return "/views/Order/order_Seat";
    }
//	}
	
	


}
