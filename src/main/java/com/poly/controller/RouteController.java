package com.poly.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.DAO.AccountDAO;
import com.poly.DAO.BusDAO;
import com.poly.DAO.BusStationDAO;
import com.poly.DAO.LocationDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.Route_StationDAO;
import com.poly.DAO.SeatDAO;
import com.poly.DAO.TicketDAO;
import com.poly.DAO.TripDAO;
import com.poly.entity.Account;
import com.poly.entity.Bus;
import com.poly.entity.Location;
import com.poly.entity.Route;
import com.poly.entity.Route_Station;
import com.poly.entity.Seat;
import com.poly.entity.Station;
import com.poly.entity.Trip;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/manager")
public class RouteController {
	
	@Autowired 
	RouteDAO routeDAO;
	
	@Autowired 
	HttpSession session;
	
	@Autowired 
	Route_StationDAO route_StationDAO;
	
	@Autowired 
	TripDAO tripDAO;
	
	@Autowired 
	SeatDAO seatDAO;
 
	@Autowired
	BusDAO busDAO; 
	
	@Autowired
	BusStationDAO stationDAO ; 
	
	@Autowired
	ObjectMapper objectMapper; 
	
	@Autowired
	AccountDAO accountDAO; 
	
	@Autowired
	LocationDAO locationDAO;
	@GetMapping("route")
	public String getRoute(Model model,@ModelAttribute("route")Route attributes,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("field") Optional<String> field,
			RedirectAttributes attributesRedirectAttributes,
			@AuthenticationPrincipal UserDetails userDetails) {
    	
    	String email = userDetails.getUsername();
    	Account account = accountDAO.findByEmail(email).get();
    	model.addAttribute("username", account.getUsername());
    	
		Route route = (attributes!=null) ? attributes : new Route();
		model.addAttribute("route", route);
		
		List<Route> routes = routeDAO.findAll();
		model.addAttribute("routeList", routes);
		
		return "/views/items/route";
	}
	
	@PostMapping("/route")
	public String saveRoute(
			RedirectAttributes attributes,
			@RequestParam("selectedStations") String selectedStations, 
	                       @ModelAttribute("route") Route route) {

	    // Tách chuỗi selectedStations thành danh sách các ID
	    List<Integer> stationIds = new ArrayList<>();
	    if (selectedStations != null && !selectedStations.isEmpty()) {
	        for (String stationId : selectedStations.split(",")) {
	            stationIds.add(Integer.parseInt(stationId));
	        }
	    }

	    // Lấy danh sách các Station từ database dựa trên danh sách ID
	    List<Station> stations = stationDAO.findAllById(stationIds); 
	    

	    // Xử lý lưu route vào database
	    
	    Route route2;
	    if (route.getId() == 0) { // Assuming 'id' is 0 for new routes
	        route2 = new Route(); // Create a new Route
		    if (routeDAO.existsByDepartureAndDestination(route.getDeparture(), route.getDestination())) {
		    	attributes.addFlashAttribute("message","Không thể tạo tuyến đường trùng nhau");
		        return "redirect:/manager/route"; 
		    }
	        route2.setName(route.getDeparture()+" - "+route.getDestination());
	        route2.setDeparture(route.getDeparture());
	        route2.setDestination(route.getDestination());
	        route2.setStations(stations);
	        routeDAO.save(route2);
	    } else {
	        route2 = routeDAO.findById(route.getId())
	                .orElseThrow(() -> new IllegalArgumentException("Invalid route ID: " + route.getId()));
	        
	        route2.getStations().clear();
		    
		    if (route2.getStations().isEmpty()) {
		    	route2.setStations(stations);
			}
	        route2.setName(route.getName());
	        route2.setDeparture(route.getDeparture());
	        route2.setDestination(route.getDestination());
	        
		    routeDAO.save(route2); 
	    }
	    
	    

	    
	    return "redirect:/manager/route";
	}
	
	@GetMapping("/route/edit")
	public String editform(Model model,
			@AuthenticationPrincipal UserDetails userDetails,
			@ModelAttribute("route")Route attributes) {
		try {
			
	    	String email = userDetails.getUsername();
	    	Account account = accountDAO.findByEmail(email).get();
	    	model.addAttribute("username", account.getUsername());
			
			Route route = (attributes!=null) ? attributes : new Route();
			model.addAttribute("route", route);
			
			List<Station> stations = stationDAO.findAll();
			model.addAttribute("stations", stations);
			
			List<Route> products = routeDAO.findAll();
			model.addAttribute("routes", products);
			
			List<Location> locations = locationDAO.findAll();
//			model.addAttribute("locations", locations);
			List<Location> locationArray = new ArrayList<>(locations);

			String jsonArray = objectMapper.writeValueAsString(locationArray);
			model.addAttribute("locations", jsonArray);
			model.addAttribute("locationArray", locations);
			
		} catch (Exception e) {
			System.out.print(e.getMessage());
			
		}
		return "/views/Route/route_Form";
	}
	
	@GetMapping("/route/edit/{id}")
	public String edit(@PathVariable("id") Integer id,Model model,
			@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes attributes ) throws JsonProcessingException {
		
    	String email = userDetails.getUsername();
    	Account account = accountDAO.findByEmail(email).get();
    	model.addAttribute("username", account.getUsername());
		
		Route route = routeDAO.findById(id).get();
		
		List<Station> list = route.getStations();	
		attributes.addFlashAttribute("route", route);
		attributes.addFlashAttribute("stationlist", list);
		return "redirect:/manager/route/edit";
	}
	@PostMapping("route/remove")
	@Transactional
	public String getremove(@ModelAttribute("route") Route route,Model model) {
		try {
			List<Trip> trips = tripDAO.findByRoute(route);
	        for (Trip trip : trips) {
	        	trip.setStations(null);
	            tripDAO.save(trip);
	            
	        }
	        tripDAO.deleteByRoute(route);
	        routeDAO.delete(route);
	        model.addAttribute("message", "Xóa trạm xe buýt thành công!");
	    } catch (Exception e) {
	    	System.out.print(e.getMessage());
	        model.addAttribute("error", "Đã xảy ra lỗi khi xóa trạm xe buýt.");
	    }
	    
	    return "redirect:/manager/route";
	}
	@PostMapping("route/reset")
	public String getMethodName() {
		return "redirect:/admin/route";
	}
	@GetMapping("route/list/{routeId}")
	public String getStationList(@PathVariable int routeId,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {
		
    	String email = userDetails.getUsername();
    	Account account = accountDAO.findByEmail(email).get();
    	model.addAttribute("username", account.getUsername());
		
		
		Route route = routeDAO.findById(routeId).get();
		List<Station> stations = route.getStations();
		model.addAttribute("stationList", stations);
		return "/views/Route/route_Station_List";
	}
	@PostMapping("route/list")
	public String findByPrice(Model model,
			   @RequestParam("departure_name")String departureName,
		        @RequestParam("destination_name")String destinationName) {
		
		Route route = new Route();
		model.addAttribute("route", route);
	
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		
		List<Bus> bus = busDAO.findAll();
		model.addAttribute("bus", bus);
		
		List<Route> list = routeDAO.findAll();
		model.addAttribute("routes", list);
		
		return "/views/items/Route";
	}
	
}
