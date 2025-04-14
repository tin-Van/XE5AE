package com.poly.DAO;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.Route;
import com.poly.entity.Seat;
import com.poly.entity.Ticket;
import com.poly.entity.Trip;
import com.poly.entity.Account;


public interface TicketDAO extends JpaRepository<Ticket, Integer> {

	@Query("SELECT COUNT(b) FROM Ticket b")
	Long countTotalTickets();
//
	@Query("SELECT SUM(r.trip.price) FROM Ticket r WHERE r.paymentstatus = 'True'")
	Long countTotalSum();
//
//	@Query("SELECT COUNT(r.customer) FROM Ticket r")
//	Long countTotalcustomer();
//
	@Query("SELECT t.trip, COUNT(t) as count FROM Ticket t " + "WHERE t.paymentstatus IN ('True') "
			+ "GROUP BY t.trip HAVING COUNT(t) >= 2 " + "ORDER BY count DESC") // Thêm các điều kiện thanh toán
																				// thànhcông
	List<Object[]> findMostBookedRoutes();
//
//	// Sửa chính tả ở phương thức tìm theo email

//
//	List<Ticket> findByUsername(String username);
//
	List<Ticket> findTop4ByOrderByPurchaseTimeDesc();
//
//	@Modifying
//	@Query("UPDATE Ticket t SET t.seat = null WHERE t.seat.id = :seatId")
//	void updateSeatToNull(@Param("seatId") int seatId);
//
//	Optional<Ticket> findByRouteAndSeat(Route route, Seat seat);


	Optional<Ticket> findByTripAndSeat(Trip trip, Seat seat);
	
	List<Ticket> findByAccount(Account account);

	List<Ticket> findByTicketCode(String ticketCode);
	
	void deleteByTripId(int tripId);

}
