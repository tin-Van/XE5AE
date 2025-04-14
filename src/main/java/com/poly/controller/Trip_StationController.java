package com.poly.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.DAO.BusStationDAO;
import com.poly.DAO.TripDAO;
import com.poly.DAO.Trip_StationDAO;
import com.poly.entity.Station;
import com.poly.entity.Trip;
import com.poly.entity.Trip_Station;



@Controller
@RequestMapping("/manager")
public class Trip_StationController {
	
	@Autowired
    private TripDAO tripDAO;

	@Autowired
    private Trip_StationDAO trip_StationDAO;
	
    @Autowired
    private BusStationDAO stationDAO;

    @GetMapping("/trip_station/{tripID}")
    public String ListTripStation(Model model,@PathVariable String tripID) {
    	
    	Trip trip = tripDAO.findById(Integer.valueOf(tripID)).get();
    	
    	List<Trip_Station> list = trip_StationDAO.findByTrip(trip);
    	model.addAttribute("trip_StationList", list);
    	return "/views/Trip_Station/trip_station_List";
    }

   
}	
