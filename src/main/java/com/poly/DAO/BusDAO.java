package com.poly.DAO;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entity.Account;
import com.poly.entity.Bus;
import com.poly.entity.Trip;

public interface BusDAO extends JpaRepository<Bus, Integer> {

	@Query("SELECT COUNT(b) FROM Bus b")
	Long countTotalBuses();

	Optional<Bus> findByname(String name);

	Bus findByName(String busName);

	List<Bus> findByTrips(Trip trip);
}
