package com.poly.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Station;
import com.poly.entity.Trip;
import com.poly.entity.Trip_Station;

public interface Trip_StationDAO extends JpaRepository<Trip_Station, Integer> {


	List<Trip_Station> findByTrip(Trip trip);
	List<Trip_Station> findByStation(Station station);
	void deleteByTrip(Trip trip);

	void deleteAllByTrip(Trip trip);

	//Trip_Station findByStation(Station station);

	List<Trip_Station> findByTripAndStation(Trip trip, Station station);

	
	void delete(Trip_Station trip_Station);
}
