package com.poly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false,columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "departure", columnDefinition = "nvarchar(255)")
    private String departure; // Địa điểm khởi hành

    @Column(name = "destination", columnDefinition = "nvarchar(255)")
    private String destination; // Địa điểm đến

    @JsonIgnore
    @OneToMany(mappedBy = "route", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trip> trips;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "route_station",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "station_id")
    )
    private List<Station> stations;
}