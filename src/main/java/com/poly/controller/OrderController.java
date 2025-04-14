package com.poly.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.DAO.AccountDAO;
import com.poly.DAO.BusDAO;
import com.poly.DAO.BusStationDAO;
import com.poly.DAO.LocationDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.SeatDAO;
import com.poly.DAO.TripDAO;
import com.poly.DAO.Trip_StationDAO;
import com.poly.entity.Account;
import com.poly.entity.Bus;
import com.poly.entity.Location;
import com.poly.entity.Route;
import com.poly.entity.Seat;
import com.poly.entity.Station;
import com.poly.entity.Ticket;
import com.poly.entity.Trip;
import com.poly.entity.Trip_Station;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("order")
public class OrderController {

	@Autowired
	private RouteDAO routeDAO;

	@Autowired
	private TripDAO tripDAO;

	@Autowired
	private BusStationDAO stationDAO;
	
	@Autowired
	private Trip_StationDAO trip_StationDAO;
	
	@Autowired
	private SeatDAO seatDAO;
	
	@Autowired
	private AccountDAO accountDAO;
	
	@Autowired
	private BusDAO busDAO;
	
	@Autowired
	private LocationDAO locationDAO;

	@Autowired
	HttpSession session;
	
	@Autowired
	ObjectMapper objectMapper; 
	
	@GetMapping("")
	public String showList(Model model,
			RedirectAttributes attributesRedirectAttributes,
			@ModelAttribute("route")Route attributes
			) throws JsonProcessingException {
		
		try {
		//Lấy lấy sách trip qua session
		@SuppressWarnings("unchecked")
		List<Trip> trips = (List<Trip>) session.getAttribute("trips");
		Route route = (attributes!=null) ? attributes : new Route();
		
		if (trips.isEmpty()) {
			model.addAttribute("message", "Không tìm thấy chuyến xe phù hợp");
			
		}
		model.addAttribute("route", route);
		model.addAttribute("trips", trips);
		
		
        // Lấy danh sách các trạm đầu tiên và cuối cùng cho mỗi chuyến xe
        List<Station> firstStations = new ArrayList<>();
        List<Station> lastStations = new ArrayList<>();
        List<String> firststopTimes = new ArrayList<>();
        List<String> laststopTimes = new ArrayList<>();

        for (Trip trip : trips) {
            List<Trip_Station> stations = trip_StationDAO.findByTrip(trip);
            //Sắp xếp lại các trạm dừng trong danh sách stations theo thứ tự stationOrder
            stations.sort(Comparator.comparingInt(Trip_Station::getStationOrder));
            if (!stations.isEmpty()) {
                firstStations.add(stations.get(0).getStation());
                firststopTimes.add(stations.get(0).getStopTime());
                lastStations.add(stations.get(stations.size() - 1).getStation());
                laststopTimes.add(stations.get(stations.size() - 1).getStopTime());
            }
        }
        
        model.addAttribute("firstStations", firstStations);
        model.addAttribute("lastStations", lastStations);
        
        model.addAttribute("firststopTimes", firststopTimes);
        model.addAttribute("laststopTimes", laststopTimes);
		
		List<Location> locations = locationDAO.findAll();
		List<Location> locationArray = new ArrayList<>(locations);

		String jsonArray = objectMapper.writeValueAsString(locationArray);
		model.addAttribute("locations", jsonArray);
		model.addAttribute("locationArray", locationDAO.findAll());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY"); 
		LocalDate aa =  (LocalDate) session.getAttribute("travelDate");
		model.addAttribute("travelDate", formatter.format(aa));
		return "views/Order/order";
		
		
		
		} catch (Exception e) {
			List<Location> locations = locationDAO.findAll();
			List<Location> locationArray = new ArrayList<>(locations);

			String jsonArray = objectMapper.writeValueAsString(locationArray);
			model.addAttribute("locations", jsonArray);
			return "/home";
		}
	}
	
	@PostMapping("")
	public String bookOrder(
			Model model,
			RedirectAttributes redirectAttributes,
			@RequestParam("seatID") String seatID,
			@RequestParam("tripID") int tripId,
			@AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
		try {
			
		
		List<Bus> bus = busDAO.findAll();
		redirectAttributes.addFlashAttribute("bus", bus);
		
		Trip trip = tripDAO.findById(tripId).get();

		
    	String email = userDetails.getUsername();
    	Account account = accountDAO.findByEmail(email).get();
		redirectAttributes.addFlashAttribute("userID", account.getUsername());
		redirectAttributes.addFlashAttribute("route", account.getUsername());
		return "redirect:/order/bookingconfirmation?userID=" + account.getUsername() + "&tripID=" + tripId + "&seatID=" + seatID;

		} catch (Exception e) {
			List<Location> locations = locationDAO.findAll();
			List<Location> locationArray = new ArrayList<>(locations);

			String jsonArray = objectMapper.writeValueAsString(locationArray);
			model.addAttribute("locations", locationDAO.findAll());
			System.out.print(e.getMessage());
			return "/home";
		}
	}
	
	@PostMapping("/search")
	public String searchRoutes(Model model,
			RedirectAttributes attributes,
			@RequestParam LocalDate time,
			@ModelAttribute Route route) {
		Route routes = routeDAO.findByDepartureAndDestination(route.getDeparture(), route.getDestination());
		List<Trip> Trips = tripDAO.findByRouteAndDepartureDate(routes, time);
		int tripCount = Trips.size();
		
		session.setAttribute("trips", Trips);
		session.setAttribute("travelDate", time);
		attributes.addFlashAttribute("route", routes);
		attributes.addFlashAttribute("tripCount", tripCount);
		attributes.addFlashAttribute("locationArray", locationDAO.findAll());
		return "redirect:/order"; // Không có dấu gạch chéo ở đầu
	}
	
	@GetMapping("bookingconfirmation")
	public String getBookingConfirmation(Model model,
			@RequestParam("userID") String userID,
			@RequestParam("tripID") int tripID,
			@RequestParam("seatID") List<Integer> seatID) throws JsonProcessingException {

		Trip trip = tripDAO.findById(tripID)
				.orElseThrow();
		Account account = accountDAO.findByUsername(userID)
				.orElseThrow();

		while (seatID.remove(null)) {
		}
		// Danh sách chứa các ghế đã chọn
		seatID.removeIf(Objects::isNull);
		List<Seat> selectedSeats = new ArrayList<Seat>();

		for (Integer id : seatID) {
			Seat seat = seatDAO.findById(id)
					.orElseThrow();
			selectedSeats.add(seat);
		}

		long count = seatID.stream().count();
		
		List<Station> stations = stationDAO.findByTrips(trip);
		List<Station> stationsArray = new ArrayList<>(stations);
		String jsonArray = objectMapper.writeValueAsString(stationsArray);
		model.addAttribute("stations", jsonArray);
		
        List<Trip_Station> tripStations = trip_StationDAO.findByTrip(trip);
        tripStations.sort(Comparator.comparingInt(Trip_Station::getStationOrder));
        model.addAttribute("tripStations", tripStations);


		model.addAttribute("ticket", new Ticket());
		model.addAttribute("trip", trip);
		model.addAttribute("account", account);
		model.addAttribute("username", account.getUsername());
		model.addAttribute("seats", seatID);
		model.addAttribute("selectedSeats", selectedSeats);
		model.addAttribute("count", count);
		return "/views/items/info_contact";
	}
	
	@PostMapping("bookingconfirmation")
	public String postbookingconfirmation(Model model,
			@ModelAttribute Ticket ticket,
			@RequestParam("email") String email,
			@RequestParam("userID") String userID,
			@RequestParam("tripID") String tripID,
			@RequestParam("count") int count,
			@RequestParam("seatID") String seatID,
			@RequestParam("customer") String customer,
			@RequestParam("phonenumber") String phonenumber)
			throws MessagingException, UnsupportedEncodingException {
		Optional<Trip> tripOptional = tripDAO.findById(Integer.valueOf(tripID));
		Optional<Account> accountOptional = accountDAO.findByEmail(email);

		if (!tripOptional.isPresent()) {
			throw new NoSuchElementException("Route not found with id " + tripID);
		}
		if (!accountOptional.isPresent()) {
			throw new NoSuchElementException("Account not found with email " + userID);
		}

		List<String> seatList = Arrays.asList(seatID.replaceAll("[\\[\\]\\s]", "").split(","));
		String seatListString = String.join(",", seatList);
		Trip trip = tripOptional.get();
		Account account = accountOptional.get();
		
		
		 String encodedDeparture = URLEncoder.encode(ticket.getDeparture(), StandardCharsets.UTF_8);
		 String encodedDestination = URLEncoder.encode(ticket.getDestination(), StandardCharsets.UTF_8);
		 String encodedCustomer = URLEncoder.encode(customer, StandardCharsets.UTF_8); 

		return "redirect:/pay?price=" + trip.getPrice() * count 
				+ "&customer=" + encodedCustomer
				+ "&phonenumber=" + phonenumber 
				+ "&email=" + email 
				+ "&tripID=" + tripID
				+ "&departure=" + encodedDeparture 
				+ "&destination=" + encodedDestination
				+ "&userID=" + account.getUsername() 
				+ "&seatID=" + seatListString ;
	}
}