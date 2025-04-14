package com.poly.entity;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Station {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "name",columnDefinition = "nvarchar(255)")
	private String name; // Tên địa điểm
	
	@Column(name = "phonenumber",columnDefinition = "nvarchar(255)")
	private String phonenumber; // Tên địa điểm
	
	@Column(name = "address",columnDefinition = "nvarchar(255)")
	private String address; // thời gian dự kiến di chuyển từ trạm trước đến trạm này (phút)
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "id_location")
	private Location location; // Địa điểm khởi hành
	
	@ManyToMany(mappedBy = "stations")
	@JsonIgnore // Thêm annotation này
    private List<Trip> trips; // Một bến xe có thể có nhiều chuyến xe dừng


	@ManyToMany(mappedBy = "stations")
	@JsonIgnore // Thêm annotation này
    private List<Route> routes; // Một bến xe có thể có nhiều chuyến xe dừng
}
