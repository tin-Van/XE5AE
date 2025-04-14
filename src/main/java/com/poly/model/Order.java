package com.poly.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	
	@NotEmpty(message = "Vui lòng địa khởi hành")
	String departure_name;
	
	@NotEmpty(message = "Vui lòng địa điểm đến")
	String destination_name;
	
	@NotNull(message = "Vui lòng ngày khởi hành")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	Date daycheck;
}
