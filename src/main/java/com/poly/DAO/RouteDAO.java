package com.poly.DAO;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Location;
import com.poly.entity.Route;
import com.poly.entity.Station;
import com.poly.entity.Trip;

public interface RouteDAO extends JpaRepository<Route, Integer> {

	Route findByName(String name);

	List<Route> findByTrips(Trip trip);
	Route findByDepartureAndDestination(String departure, String destination);
	
	List<Route> findByStations(Station station);

	boolean existsByDepartureAndDestination(String departure, String destination);

}
