package com.poly.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.poly.entity.Bus;
import com.poly.entity.Station;
import com.poly.entity.Route;
import com.poly.entity.Trip;
import java.time.LocalDate;

@Repository
public interface TripDAO extends JpaRepository<Trip, Integer> {

	List<Trip> findByBus(Bus bus);

	List<Trip> findByRouteAndDepartureDate(Route route, LocalDate departureDate);

	List<Trip> findByStations(Station station);

	void deleteByBus(Bus bus);

	List<Trip> findByRoute(Route route);

	void deleteByRoute(Route route);

	List<Trip> findByRouteId(int routeId);
	
    @Query("SELECT COUNT(b) FROM Trip b")
    Long countTotalTrip();
}
