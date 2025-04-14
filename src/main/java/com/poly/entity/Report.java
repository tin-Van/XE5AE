package com.poly.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
	
	@Id
	String route_name;
	
	Double sum;
	
	Long count;
	
	private Integer month;
	
	private Double sumPrice;
	
	public Report(Integer month, Double sumPrice) {
        this.month = month;
        this.sumPrice = sumPrice;
    }
	 public Report(String routeName, Double sum, Long count) {
	        this.route_name = routeName;
	        this.sum = sum;
	        this.count = count;
	    }
}