package com.poly.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", columnDefinition = "nvarchar(255)")
    @NotBlank(message = "Tên xe không được để trống")
    private String name;

    @Column(name = "license_plate", columnDefinition = "nvarchar(255)")
    @NotBlank(message = "Biển số xe không được để trống")
    private String licensePlate;

    @Column(name = "model", columnDefinition = "nvarchar(255)")
    @NotBlank(message = "Mẫu xe không được để trống")
    private String model;
    
    @Column(name = "capacity")
    @Min(value = 1, message = "Sức chứa phải lớn hơn 0")
    private int capacity;

    @Column(name = "avatar", columnDefinition = "nvarchar(255)")
    private String avatar;

    @JsonIgnore
    @OneToMany(mappedBy = "bus",orphanRemoval = true)
    private List<Trip> trips; 
    
    @JsonIgnore
    @OneToMany(mappedBy = "bus")
    private List<Seat> seats; 
    
    public String getDisplayName() {
        return name + " - sức chứa : " + capacity;
    }
}
