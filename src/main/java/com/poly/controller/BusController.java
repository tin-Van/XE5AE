package com.poly.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.BusDAO;
import com.poly.DAO.LocationDAO;
import com.poly.DAO.TripDAO;
import com.poly.entity.Account;
import com.poly.entity.Bus;
import com.poly.entity.Location;
import com.poly.entity.Route;
import com.poly.entity.Trip;
import com.poly.entity.Trip_Station;
import com.poly.service.HomeService;

import jakarta.transaction.Transactional;

@Controller
public class BusController {

	@Autowired
	BusDAO busDAO;

	@Autowired
	TripDAO tripDAO;
	
	@Autowired
	AccountDAO accountDAO;

	@Autowired
	HomeService service;

	@Autowired
	LocationDAO locationDAO;

	@GetMapping("/manager/bus")
	public String getRoute(Model model,
			@ModelAttribute("bus") Bus attributes,
    		//annotation để lấy thông tin về người dùng hiện tại đã được xác thực từ Security Context 
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
		String email = userDetails.getUsername();
		Account account = accountDAO.findByEmail(email).get();
		model.addAttribute("username", account.getUsername());
		
		//xét giá trị attributes nếu ko null thì lấy attributes ko thì new attributes
		Bus bus = (attributes != null) ? attributes : new Bus();
		model.addAttribute("bus", bus);

		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);

		List<Bus> busList = busDAO.findAll();
		model.addAttribute("busList", busList);
		} catch (Exception e) {
			model.addAttribute("message", "Vui lòng đăng nhập lại để tiếp tục");
			return "redirect:/login";
		}
		return "/views/items/bus";
	}

	@PostMapping("/manager/bus")
	@Transactional
	public String save(@ModelAttribute("bus")Bus bus, Model model, @RequestParam("file") MultipartFile attach) {
		
	    if (bus.getName() != null) { // Kiểm tra xem bus đã tồn tại chưa
	        Bus existingBus = busDAO.findById(bus.getId()).orElse(null);
	        if (existingBus != null) {
	            // Cập nhật những trường cần thiết
	            existingBus.setName(bus.getName());
	            existingBus.setCapacity(bus.getCapacity());
	            existingBus.setModel(bus.getModel());
	            existingBus.setLicensePlate(bus.getLicensePlate());
	            
	            //Kiểm tra có ảnh ko và kiểm tra file ảnh
	            if (!attach.isEmpty() && attach.getContentType().startsWith("image/")) {
	            	//lấy tên ảnh 
	                String filename = attach.getOriginalFilename();
	                existingBus.setAvatar(filename);
	                service.saveImage(attach);
	            }

	            busDAO.save(existingBus); // Lưu thay đổi
	            return "redirect:/manager/bus";
	        }
	    }

	    // Nếu bus chưa tồn tại, tạo mới và lưu
	    if (!attach.isEmpty() && attach.getContentType().startsWith("image/")) {
	        String filename = attach.getOriginalFilename();
	        bus.setAvatar(filename);
	        service.saveImage(attach);
	    }
	    busDAO.save(bus);
	    return "redirect:/manager/bus";
	}

	@GetMapping("/manager/bus/edit")
	public String editform(Model model,
		@ModelAttribute("bus") Bus attributes, 
		@AuthenticationPrincipal UserDetails userDetails) {
		
		String email = userDetails.getUsername();
		Account account = accountDAO.findByEmail(email).get();
		model.addAttribute("username", account.getUsername());
		
		
		Bus bus = (attributes != null) ? attributes : new Bus();
		model.addAttribute("bus", bus);
		model.addAttribute("file", bus.getAvatar());
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);

		List<Bus> busList = busDAO.findAll();
		model.addAttribute("busList", busList);
		return "/views/Bus/bus_Form";
	}

	@GetMapping("/manager/bus/edit/{id}")
	public String edit(@PathVariable("id") Integer id, Model model,
			@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes attributes) {
		
		String email = userDetails.getUsername();
		Account account = accountDAO.findByEmail(email).get();
		model.addAttribute("username", account.getUsername());
		
		Bus bus = busDAO.findById(id).get();
		attributes.addFlashAttribute("bus", bus);
		attributes.addFlashAttribute("file", bus.getAvatar());
		return "redirect:/manager/bus/edit";
	}

	@PostMapping("manager/bus/reset")
	public String getMethodName() {
		Bus bus = new Bus();
		return "redirect:/manager/bus/edit";
	}

	@PostMapping("manager/bus/remove")
	@Transactional
	public String getRemove(@ModelAttribute("bus") Bus bus, Model model) {
	    try {     
	    	//Xóa các bảng liên quan với Bus
	        List<Trip> trips = tripDAO.findByBus(bus);
	        for (Trip trip : trips) {
	        	trip.setStations(null);
	            tripDAO.save(trip);
	            
	        }
	        tripDAO.deleteByBus(bus);
	        busDAO.delete(bus);
	    } catch (Exception e) {
	        System.out.print(e.getMessage());
	        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xóa bus: " + e.getMessage());
	    }

	    return "redirect:/manager/bus";
	}
}
