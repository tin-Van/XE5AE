package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

	@Column(name = "payment_status")
	private String paymentstatus;
	
	@Column(name = "ticket_Code", columnDefinition = "nvarchar(255)")
	private String ticketCode;
    
	@Column(name = "departure", columnDefinition = "nvarchar(255)")
	private String departure;

	@Column(name = "email", columnDefinition = "nvarchar(255)")
	private String email;
	
	@Column(name = "destination", columnDefinition = "nvarchar(255)")
	private String destination;
	
	@Column(name = "customer", columnDefinition = "nvarchar(255)")
	private String customer;

	@Column(name = "phonenumber", columnDefinition = "nvarchar(255)")
	private String phonenumber;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false) // Thêm liên kết với Seat
    private Seat seat; // Thêm thuộc tính seat
    
    @Column(nullable = false)
    private LocalDateTime purchaseTime;
    
    
    public class TicketCodeGenerator {
		// Tạo bộ ký tự bao gồm chữ cái và số
		private static final String CHARACTERS = "0123456789";
		private static final int CODE_LENGTH = 8;

		// Phương thức để sinh mã đơn vé
		public static String generateTicketCode() {
			SecureRandom random = new SecureRandom();
			StringBuilder ticketCode = new StringBuilder(CODE_LENGTH);

			for (int i = 0; i < CODE_LENGTH; i++) {
				int index = random.nextInt(CHARACTERS.length());
				ticketCode.append(CHARACTERS.charAt(index));
			}

			return ticketCode.toString();
		}
	}

	
	  public String getFormattedBookingDate() { if (purchaseTime != null) {
	  DateTimeFormatter formatter =
	  DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy"); return
	  purchaseTime.format(formatter); } return "N/A";
	  }
	 
	public void setnameBus(String string) {
		// TODO Auto-generated method stub

	}

	public Object getSomeRequiredField() {
		// TODO Auto-generated method stub
		return null;
	}

}
