package com.poly.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entity.Report;
import com.poly.entity.Ticket;

public interface ReportService extends JpaRepository<Ticket, Integer> {
	@Query("SELECT new com.poly.entity.Report(o.trip.route.name, SUM(o.trip.price), COUNT(o)) "
		       + "FROM Ticket o "
		       + "WHERE o.paymentstatus = 'True' "
		       + "GROUP BY o.trip.route.name " 
		       + "ORDER BY SUM(o.trip.price) DESC, COUNT(o) DESC")
		List<Report> getInventoryByCategory();


	 // Truy vấn để lấy tổng doanh thu theo từng tháng trong năm
	@Query("SELECT new com.poly.entity.Report( " +
		       "EXTRACT(MONTH FROM t.purchaseTime), " + 
		       "SUM(t.trip.price)) " + 
		       "FROM Ticket t " +
		       "WHERE EXTRACT(YEAR FROM t.purchaseTime) = :year " + 
		       "GROUP BY EXTRACT(MONTH FROM t.purchaseTime) " + 
		       "ORDER BY EXTRACT(MONTH FROM t.purchaseTime)")
		List<Report> findRevenueByMonthOfYear(int year);


    
    
}



