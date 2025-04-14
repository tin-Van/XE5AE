package com.poly.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.SeatDAO;
import com.poly.DAO.TicketDAO;
import com.poly.DAO.TripDAO;
import com.poly.config.Config;
import com.poly.entity.Account;
import com.poly.entity.Route;
import com.poly.entity.Seat;
import com.poly.entity.Ticket;
import com.poly.entity.Ticket.TicketCodeGenerator;
import com.poly.entity.Trip;
import com.poly.service.MailSenderService;

import jakarta.mail.MessagingException;

@Controller
public class PaymentController {
	@Autowired
	RouteDAO routeDAO;
	@Autowired
	AccountDAO accountDAO;
	@Autowired
	TripDAO tripDAO;
	@Autowired
	TicketDAO ticketDAO;
	@Autowired
	SeatDAO seatDAO;
	@Autowired
	MailSenderService mailSenderService;

	@GetMapping("/pay")

	public String getPay(
			@RequestParam String departure,
			@RequestParam String destination,
			@RequestParam String price,
			@RequestParam String tripID,
			@RequestParam String userID,
			@RequestParam String email,
			@RequestParam String seatID,
			@RequestParam String customer,
			@RequestParam String phonenumber,
			@RequestParam(value = "status", required = false, defaultValue = "unpaid") String status)
			throws UnsupportedEncodingException {
		 String decodedCustomer = URLDecoder.decode(customer, StandardCharsets.UTF_8); 
		 String decodedDeparture = URLDecoder.decode(departure, StandardCharsets.UTF_8); 
		 String decodedDestination = URLDecoder.decode(destination, StandardCharsets.UTF_8); 
		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String orderType = "other";
		long amount = (long) (Double.parseDouble(price) * 100);
		String bankCode = "NCB";

		String vnp_TxnRef = Config.getRandomNumber(8);
		String vnp_IpAddr = "127.0.0.1";

		String vnp_TmnCode = Config.vnp_TmnCode;

		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", vnp_Version);
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(amount));
		vnp_Params.put("vnp_CurrCode", "VND");

		vnp_Params.put("vnp_BankCode", bankCode);
		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
		vnp_Params.put("vnp_OrderType", orderType);

		vnp_Params.put("vnp_Locale", "vn");
		vnp_Params.put("vnp_ReturnUrl", Config.getVnpReturnUrl(userID, tripID, status, seatID, email,decodedDeparture,decodedDestination, decodedCustomer, phonenumber));
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_CreateDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

		cld.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

		List fieldNames = new ArrayList(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				// Build hash data
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
				// Build query
				query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();
		String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		return "redirect:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?" + queryUrl;

	}

	@GetMapping("/comfirm")
	public String getcomfirm(Model model,
			@RequestParam("email") String email,
			@RequestParam("userID") String userID,
			@RequestParam("tripID") String tripID,
			@RequestParam("seatID") String seatID,
			@RequestParam("customer") String customer,
			@RequestParam("departure") String departure,
			@RequestParam("destination") String destination,
			@RequestParam("phonenumber") String phonenumber,
			@RequestParam("vnp_Amount") String vnpAmount,
			@RequestParam("vnp_BankCode") String vnpBankCode,
			@RequestParam("vnp_OrderInfo") String vnpOrderInfo,
			@RequestParam("vnp_ResponseCode") String vnpResponseCode, @RequestParam("vnp_TmnCode") String vnpTmnCode,
			@RequestParam("vnp_TransactionStatus") String vnpTransactionStatus,
			@RequestParam(value = "status", required = false, defaultValue = "unpaid") String status)
			throws MessagingException {
		customer = URLDecoder.decode(customer, StandardCharsets.UTF_8); 
		departure = URLDecoder.decode(departure, StandardCharsets.UTF_8); 
		destination = URLDecoder.decode(destination, StandardCharsets.UTF_8); 
		StringBuilder ticketCodesBuilder = new StringBuilder();
		List<String> seatList = Arrays.asList(seatID.replaceAll("[\\[\\]\\s]", "").split(","));
		Optional<Trip> tripOptional = tripDAO.findById(Integer.valueOf(tripID));
		Optional<Account> accountOptional = accountDAO.findByUsername(userID);

		if (!tripOptional.isPresent() || !accountOptional.isPresent()) {
			model.addAttribute("error", "Không tìm thấy thông tin tài khoản hoặc tuyến đường.");
			return "error"; // Trả về trang error.html với thông báo lỗi
		}

		Trip trip = tripOptional.get();
		Account account = accountOptional.get();

		// Danh sách chứa thông tin các vé
		List<Ticket> tickets = new ArrayList<>();

		// Xử lý vé cho từng ghế
		for (String seatNumber : seatList) {
			Optional<Seat> seatOptional = seatDAO.findById(Integer.valueOf(seatNumber));
			if (!seatOptional.isPresent()) {
				model.addAttribute("error", "Không tìm thấy ghế với ID: " + seatNumber);
				return "error";
			}

			Seat seat = seatOptional.get();
			 
			// Kiểm tra xem vé đã tồn tại hay chưa
			Optional<Ticket> existingTicketOptional = ticketDAO.findByTripAndSeat(trip, seat);
			Ticket ticket;
			if (existingTicketOptional.isPresent()) {
				ticket = existingTicketOptional.get();
			} else {
				
				ticket = new Ticket();
				ticket.setTrip(trip);
				ticket.setEmail(email);
				ticket.setAccount(account);
				ticket.setCustomer(customer);
				ticket.setPhonenumber(phonenumber);
				ticket.setPurchaseTime(LocalDateTime.now());
				ticket.setSeat(seat);
				ticket.setDeparture(departure);
				ticket.setDestination(destination);
				String ticketCode = TicketCodeGenerator.generateTicketCode();
				ticket.setTicketCode(ticketCode);
			}

			// Cập nhật trạng thái thanh toán
			if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
				ticket.setPaymentstatus("True");
				seat.setBooked(true); // Đặt trạng thái ghế là đã đặt
				
				
				String subject = "Hóa Đơn Điện Tử";
				String body = "<html>" + "<head>" + "<style>"
						+ "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }"
						+ ".container { background-color: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }"
						+ ".header { text-align: center; color: #4CAF50; }"
						+ ".footer { margin-top: 20px; text-align: center; font-size: 0.9em; color: #777; }"
						+ ".bold { font-weight: bold; }" + "</style>" + "</head>" + "<body>" + "<div class='container'>"
						+ "<h2 class='header'>Hóa Đơn Điện Tử</h2>" + "<p>Cảm ơn bạn đã mua hàng, <span class='bold'>"
						+ customer + "</span>!</p>" + "<p>Thông tin vé của bạn:</p>" + "<ul>"
						+ "<li><span class='bold'>Mã vé xe:</span> " + ticket.getTicketCode() + "</li>"
						+ "<li><span class='bold'>Ghế số:</span> " + seat.getSeatNumber() + "</li>"
						+ "<li><span class='bold'>Địa điểm khởi hành:</span> " + trip.getRoute().getDeparture() + "</li>"
						+ "<li><span class='bold'>Địa điểm đến:</span> " + trip.getRoute().getDestination() + "</li>"
						+ "<li><span class='bold'>Thời gian khởi hành:</span> " + trip.getStartTime() + "</li>"
						+ "<li><span class='bold'>Ngày khởi hành:</span> " + trip.getDepartureDate() + "</li>"
						+ "<li><span class='bold'>Giá tiền:</span> " + trip.getPrice() + " VND</li>" + "</ul>"
						+ "<p><span class='bold'>Trạng thái thanh toán:</span> "
						+ (ticket.getPaymentstatus().equals("True") ? "Thanh Toán online" : "Thanh toán thất bại") + "</p>"
						+ "<p>Chúc bạn có chuyến đi vui vẻ!</p>" + "</div>" + "<div class='footer'>"
						+ "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" + "</div>" + "</body>" + "</html>";

				// Gửi email cho khách hàng
				mailSenderService.senEmailConfirm(email, subject, body);
			} else {
				ticket.setPaymentstatus("False");
				seat.setBooked(false); // Đặt trạng thái ghế là chưa đặt
			}

			seatDAO.save(seat);
			ticketDAO.save(ticket);
			tickets.add(ticket); // Thêm vé vào danh sách


		}

		// Thêm thông báo vào model
		if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
			model.addAttribute("message", "Đặt vé thành công! Hóa đơn sẽ được gửi về mail của bạn.");

		} else {
			model.addAttribute("message", "Thanh toán không thành công. Mã phản hồi: " + vnpResponseCode);
		}

		return "/views/items/confirmation.html"; // Trả về trang xác nhận sau khi xử lý
	}
}
