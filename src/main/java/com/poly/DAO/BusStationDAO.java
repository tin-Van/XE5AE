package com.poly.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Station;
import com.poly.entity.Trip;

public interface BusStationDAO extends JpaRepository<Station, Integer> {

	List<Station> findByTrips(Trip trip);


}
