package com.poly.DAO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Location;

public interface LocationDAO extends JpaRepository<Location, Integer> {

	Page<Location> findByNameLike(String name,Pageable pageable);
}
