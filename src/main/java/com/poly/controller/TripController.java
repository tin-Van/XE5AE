package com.poly.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.DAO.AccountDAO;
import com.poly.DAO.BusDAO;
import com.poly.DAO.BusStationDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.SeatDAO;
import com.poly.DAO.TicketDAO;
import com.poly.DAO.TripDAO;
import com.poly.DAO.Trip_StationDAO;
import com.poly.entity.Account;
import com.poly.entity.Bus;
import com.poly.entity.Route;
import com.poly.entity.Seat;
import com.poly.entity.Station;
import com.poly.entity.Trip;
import com.poly.entity.Trip_Station;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/manager")
public class TripController {

	@Autowired
	private TripDAO tripDAO;

	@Autowired
	private TicketDAO ticketDAO;

	@Autowired
	private RouteDAO routeDAO;

	@Autowired
	private SeatDAO seatDAO;

	@Autowired
	private BusDAO busDAO;

	@Autowired
	private HttpServletResponse response;

	@Autowired
	private AccountDAO accountDAO;

	@Autowired
	private BusStationDAO stationDAO;

	@Autowired
	private Trip_StationDAO trip_StationDAO;

	@Autowired
	ObjectMapper objectMapper;
	
	
	
	
	//Lưu Chuyến
	@SuppressWarnings("unlikely-arg-type")
	@PostMapping("/trip")
	@Transactional
	public String saveTrip(
			RedirectAttributes redirectAttributes,
			@RequestParam("selectedStations") List<Integer> stationIds,
			@RequestParam Map<String, String> allParams, @RequestParam("busName") String busName,
			@RequestParam("routeName") String routeName, @ModelAttribute Trip tripParam) {

		try {
			Trip trip;
			if (tripParam.getId() > 0) {
				// Chỉnh sửa Trip
				trip = tripDAO.findById(tripParam.getId()).orElseThrow();
				
                for (Seat seat : trip.getSeats()) {
                    seatDAO.delete(seat);
                }
                trip.getSeats().clear();
			} else {
				// Thêm mới Trip
				trip = new Trip();
			}

			// Cập nhật thông tin chung của Trip
			trip.setDepartureDate(tripParam.getDepartureDate());
			trip.setPrice(tripParam.getPrice());
			trip.setBus(tripParam.getBus());
			trip.setRoute(tripParam.getRoute());
			// Xử lý Trip_Station
			List<Station> stations = stationDAO.findAllById(stationIds);

			// Thêm hoặc cập nhật Trip_Station
			int lastStationOrder = stations.size(); 
			for (Station station : stations) {

				if (trip.getTripStations() == null) {
					trip.setTripStations(new ArrayList<>());
				}
				// Kiểm tra xem Trip_Station đã tồn tại chưa
				Optional<Trip_Station> existingTripStation = trip.getTripStations().stream()
						.filter(ts -> ts.getStation().getId() == station.getId()).findFirst();

				Trip_Station tripStation;
				if (existingTripStation.isPresent()) {
					// Nếu đã tồn tại, cập nhật thông tin
					tripStation = existingTripStation.get();
				} else {
					// Nếu chưa tồn tại, tạo mới
					tripStation = new Trip_Station();
					tripStation.setTrip(trip);
					tripStation.setStation(station);
					trip.getTripStations().add(tripStation);
				}

				int order = Integer.valueOf(allParams.getOrDefault("stationOrder_" + station.getId(), "0"));
				String stopTime = allParams.getOrDefault("stopTime_" + station.getId(), null);
				
				
				
				tripStation.setStationOrder(order);
				tripStation.setStopTime(stopTime);
				
				if (order == 1) { 
					LocalTime time = LocalTime.parse(stopTime);
			       trip.setStartTime(time);
			    }
				if (order == lastStationOrder) { 
					LocalTime time = LocalTime.parse(stopTime);
			       trip.setEndTime(time);
			    }
			}
			tripDAO.save(trip);
			Bus bus = busDAO.findById(tripParam.getBus().getId()).get();
			System.out.print("asda:" + tripParam.getBus().getCapacity());
			Optional<Seat> existingSeat = seatDAO.findByTrip(trip);
			for (int i = 1; i < bus.getCapacity(); i++) {
				if (existingSeat.isEmpty()) {
					Seat seat = new Seat();
					seat.setSeatNumber("Ghế " + i);
					seat.setSeatType("Standard");
					seat.setBooked(false);
					seat.setBus(bus);
					seat.setTrip(trip);
					seatDAO.save(seat);
				}
			}
			
		} catch (Exception e) {
			// Xử lý lỗi
			System.out.print(e.getMessage());
			e.printStackTrace();
			// Ví dụ: log lỗi và return "redirect:/error";
		}

		return "redirect:/manager/route";
	}

	// Lấy danh sách chuyến của tuyến xe
	@GetMapping("/trips/list/{routeId}")
	public String listTrips(@PathVariable int routeId, @AuthenticationPrincipal UserDetails userDetails, Model model) {

		String email = userDetails.getUsername();
		Account account = accountDAO.findByEmail(email).get();
		model.addAttribute("username", account.getUsername());

		List<Trip> trips = tripDAO.findByRouteId(routeId);
		model.addAttribute("trips", trips);
		model.addAttribute("route", routeDAO.findById(routeId).get());
		return "/views/Trip/trip_List";
	}
	
	// Vào Form mới để tạo chuyến của tuyến xe
	@GetMapping("/trip/{routeId}")
	public String showTripFormByRoute(
			@PathVariable int routeId,
			Model model, RedirectAttributes attributes,
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
			Route route = routeDAO.findById(routeId).get();
			String email = userDetails.getUsername();
			Account account = accountDAO.findByEmail(email).get();
			
			
			model.addAttribute("username", account.getUsername());
			
			model.addAttribute("route", route);
	        Trip trip = new Trip();
	        trip.setRoute(route);  // Gán route cho trip

	        model.addAttribute("trip", trip);  // Thêm trip vào model
			model.addAttribute("stations", route.getStations());

			List<Bus> busArray = new ArrayList<>(busDAO.findAll());
			String busjsonArray = objectMapper.writeValueAsString(busArray);
			model.addAttribute("buslist", busjsonArray);
			model.addAttribute("busArray", busArray);

			List<Route> routeArray = new ArrayList<>(routeDAO.findAll());
			String routejsonArray = objectMapper.writeValueAsString(routeArray);
			model.addAttribute("routelist", routejsonArray);
			

			
		} catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
		}

		return "/views/Trip/trip_Form";
	}
	
	
	// Chỉnh sửa Chuyến
	@GetMapping("/trips/edit/{tripId}")
	public String editTrip(@PathVariable int tripId, @AuthenticationPrincipal UserDetails userDetails, Model model) {
		try {

			String email = userDetails.getUsername();
			Account account = accountDAO.findByEmail(email).get();
			model.addAttribute("username", account.getUsername());

			Trip trip = tripDAO.findById(tripId).get();
			model.addAttribute("trip", trip);
			model.addAttribute("route", routeDAO.findById(trip.getRoute().getId()).get());
			model.addAttribute("stations", trip.getRoute().getStations());
			model.addAttribute("name", trip.getRoute().getName());
			model.addAttribute("busName", trip.getBus().getName());
			model.addAttribute("stationlist", trip.getStations());
			

			
			
			List<Trip_Station> trip_Stations = trip_StationDAO.findByTrip(trip);
			Map<String, Object> valuesMap = new HashMap<>();
			for (Trip_Station list : trip_Stations) {
				valuesMap.put("stationOrder_" + list.getStation().getId(), list.getStationOrder());
				valuesMap.put("stopTime_" + list.getStation().getId(), list.getStopTime());
				valuesMap.put("tripStationID_" + list.getStation().getId(), list.getId());
			}
			model.addAttribute("valuesMap", valuesMap);
			List<Bus> busArray = new ArrayList<>(busDAO.findAll());
			String busjsonArray = objectMapper.writeValueAsString(busArray);
			model.addAttribute("buslist", busjsonArray);
	        model.addAttribute("busArray", busArray);
			List<Route> routeArray = new ArrayList<>(routeDAO.findAll());
			String routejsonArray = objectMapper.writeValueAsString(routeArray);
			model.addAttribute("routelist", routejsonArray);

		} catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
			Trip trip = tripDAO.findById(tripId).get();
			return "redirect:/manager/trips/list/"+ trip.getRoute().getId();

		}
		return "/views/Trip/trip_Form";
	}
	
	// Xóa Trạm của Chuyến
	@PostMapping("/trip/removeStation")
	@Transactional
	@ResponseBody
	public String deleteTripStation(@RequestParam("tripId") int tripId, @RequestParam("stationId") int stationId, Model model) {
		int id = 0;
		try {
			// Lấy Trip từ database
			Trip trip = tripDAO.findById(tripId).orElseThrow(() -> new Exception("Trip not found"));

			Station station = stationDAO.findById(stationId).orElseThrow(() -> new Exception("Station not found"));

			List<Trip_Station> trip_Stations = trip_StationDAO.findByTripAndStation(trip, station);
			if (trip_Stations.isEmpty()) {
				throw new Exception("No Trip_Station found for the given trip and station");
			}

			// Xóa các Trip_Station
			for (Trip_Station trip_Station : trip_Stations) {
				trip_Station.setStation(null);
				trip_Station.setTrip(null);
				trip_Station.setStopTime(null);
				trip_Station.setStationOrder(0);

				trip_StationDAO.save(trip_Station);
				trip_StationDAO.delete(trip_Station);
			}

			return "success";
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			System.out.print(e.getMessage());
			System.out.print("id:" + id);
			return "error: " + e.getMessage();
		}
	}
	// Xóa Chuyến
	@PostMapping("/trip/remove")
	@Transactional
	public String deleteTrips(@RequestParam int tripId, Model model) {
		try {
			Trip trip = tripDAO.findById(tripId)
					.orElseThrow(() -> new IllegalArgumentException("Chuyến đi không tồn tại"));
			ticketDAO.deleteByTripId(tripId);
			seatDAO.deleteByTripId(tripId);
			trip.setStations(null);
			tripDAO.flush();
			tripDAO.delete(trip);
			tripDAO.flush();

			model.addAttribute("trip", new Trip());
			model.addAttribute("stations", new ArrayList<>());
			model.addAttribute("message", "Xóa chuyến đi thành công!");
			List<Bus> busArray = new ArrayList<>(busDAO.findAll());
			String busjsonArray = objectMapper.writeValueAsString(busArray);
			model.addAttribute("buslist", busjsonArray);

			List<Route> routeArray = new ArrayList<>(routeDAO.findAll());
			String routejsonArray = objectMapper.writeValueAsString(routeArray);
			model.addAttribute("routelist", routejsonArray);
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			response.setHeader("Expires", "0"); // Proxies.
			return "redirect:/manager/trips/list/" + trip.getRoute().getId();
		} catch (Exception e) {
			model.addAttribute("error", "Đã xảy ra lỗi khi xóa chuyến đi.");
			System.out.print(e.getMessage());
			// Đảm bảo trả về trang hợp lệ
			return "home";
		}
	}

}

