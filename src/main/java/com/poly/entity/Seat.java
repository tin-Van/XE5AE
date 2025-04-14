package com.poly.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "seat_number",columnDefinition = "nvarchar(255)")
    private String seatNumber;

    @Column(name = "seat_type",columnDefinition = "nvarchar(255)")
    private String seatType;

    @Column(name = "is_booked")
    private boolean isBooked;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip; // Xe buýt mà ghế này thuộc về
    
    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus; // Xe buýt mà ghế này thuộc về
    
    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
}