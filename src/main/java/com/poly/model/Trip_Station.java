package com.poly.model;

import java.time.LocalTime;
import java.util.List;

import com.poly.entity.Station;
import com.poly.entity.Route;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip_Station {
    private int tripId;
    private int stationId;
}
