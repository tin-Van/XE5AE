package com.poly.DAO;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.poly.entity.Bus;
import com.poly.entity.Route;
import com.poly.entity.Seat;
import com.poly.entity.Trip;

import java.util.List;
import java.util.Optional;


public interface SeatDAO extends JpaRepository<Seat, Integer> {

	Optional<Seat> findByTrip(Trip trip);
//
//	List<Seat> findByRoute(Route route);
//
//	void save(List<Seat> list);
//	//
////	List<Seat> findByBus(Bus bus);
////
////	List<Seat> findByBusId(Integer integer);
//
//	List<Seat> findByRouteId(Integer integer);
//
//
//	Seat findBySeatNumber(String seat);
//	
//	void deleteAllByRoute(Route route);
//
//	void deleteByRoute(Route route);
//
//	@Modifying
//	@Query("DELETE FROM Seat s WHERE s.route.id = :routeId")
//	void deleteByRouteId(int routeId);

	List<Seat> findByTripId(int tripId);

	void deleteByTripId(int tripId);


	

}
