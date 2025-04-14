package com.poly.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.BusStationDAO;
import com.poly.DAO.LocationDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.Route_StationDAO;
import com.poly.DAO.TripDAO;
import com.poly.DAO.Trip_StationDAO;
import com.poly.entity.Station;
import com.poly.entity.Account;
import com.poly.entity.Location;
import com.poly.entity.Route;
import com.poly.entity.Route_Station;
import com.poly.entity.Seat;
import com.poly.entity.Trip;
import com.poly.entity.Trip_Station;

import jakarta.transaction.Transactional;
import jakarta.websocket.server.PathParam;

@Controller
@RequestMapping("/manager")
public class StationController {

	@Autowired
	BusStationDAO stationDAO;

	@Autowired
	Trip_StationDAO trip_StationDAO;

	@Autowired
	Route_StationDAO route_StationDAO;

	@Autowired
	AccountDAO accountDAO;

	@Autowired
	LocationDAO locationDAO;

	@Autowired
	TripDAO tripDAO;

	@Autowired
	RouteDAO routeDAO;

	@GetMapping("/station")
	public String BusStation(Model model, @AuthenticationPrincipal UserDetails userDetails) {

		String email = userDetails.getUsername();
		Account account = accountDAO.findByEmail(email).get();

		List<Station> busStations = stationDAO.findAll();
		List<Location> listLocations = locationDAO.findAll();

		model.addAttribute("username", account.getUsername());
		model.addAttribute("locations", listLocations);
		model.addAttribute("StationsList", busStations);
		return "/views/items/station";
	}

	@PostMapping("/station")
	public String Save(Model model, @ModelAttribute Station station) {
		stationDAO.save(station);
		return "redirect:/manager/station";
	}

	@GetMapping("/station/edit/{id}")
	public String edit(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") Integer id) {

		String email = userDetails.getUsername();
		Account account = accountDAO.findByEmail(email).get();

		Station station = stationDAO.findById(id).get();
		List<Location> listLocations = locationDAO.findAll();
		model.addAttribute("username", account.getUsername());
		model.addAttribute("locations", listLocations);
		model.addAttribute("station", station);

		return "/views/station/station_Form";
	}

	@GetMapping("/station/edit")
	public String BusStationForm(Model model, @AuthenticationPrincipal UserDetails userDetails,
			@ModelAttribute Station station) {
		model.addAttribute("station", station);
		String email = userDetails.getUsername();
		Account account = accountDAO.findByEmail(email).get();
		List<Location> listLocations = locationDAO.findAll();
		model.addAttribute("locations", listLocations);
		model.addAttribute("username", account.getUsername());
		return "/views/station/station_Form";
	}

	@PostMapping("/station/delete")
	// @Transactional
	public String deleteStation(Model model, RedirectAttributes attributes, @ModelAttribute Station station) {
		try {
			// Lấy Station từ database
			Station station2 = stationDAO.findById(station.getId())
					.orElseThrow(() -> new IllegalArgumentException("Invalid station Id:" + station.getId()));

			List<Trip> trips = station2.getTrips();
			for (Trip trip : trips) {
				List<Trip_Station> trip_Station = trip_StationDAO.findByTripAndStation(trip, station2);
				// trip.getStations().remove(station2);
				trip_StationDAO.deleteAll(trip_Station);
				// tripDAO.save(trip); // Lưu thay đổi vào Trip
			}
			List<Route> routes = station2.getRoutes();
			for (Route route : routes) {
				route.getStations().remove(station2);
				routeDAO.save(route); // Lưu thay đổi vào Trip
			}

			stationDAO.delete(station2);
			stationDAO.flush();

			attributes.addFlashAttribute("message", "Xóa trạm xe buýt thành công!");
		} catch (Exception e) {
			System.out.print(e.getMessage());
			model.addAttribute("error", "Đã xảy ra lỗi khi xóa trạm xe buýt.");
		}
		return "redirect:/manager/station";
	}

	// tìm trạm theo chuyến
	@GetMapping("/trips/{tripId}/stations")
	public String getSeatsByTrip(@PathVariable int tripId, Model model) {
		Optional<Trip> tripOptional = tripDAO.findById(tripId);
		if (!tripOptional.isPresent()) {
			// Handle case where route is not found
			model.addAttribute("error", "trip not found");
			return "error";
		}

		List<Trip_Station> stations = trip_StationDAO.findByTrip(tripOptional.get());
		stations.sort(Comparator.comparingInt(Trip_Station::getStationOrder));

		Trip trip = tripOptional.get();
		
		
		
		model.addAttribute("trip", trip);
		model.addAttribute("stations", stations);
		return "/views/Order/order_Station";
	}

}
