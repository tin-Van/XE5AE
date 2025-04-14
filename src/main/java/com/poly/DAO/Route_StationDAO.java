package com.poly.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Route;
import com.poly.entity.Route_Station;
import com.poly.entity.Station;
import com.poly.entity.Trip;
import com.poly.entity.Trip_Station;

public interface Route_StationDAO extends JpaRepository<Route_Station, Integer> {

	List<Route_Station> findByRoute(Route route);

	List<Route_Station> findByStation(Station station);


}
