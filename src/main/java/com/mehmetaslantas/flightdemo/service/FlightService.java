package com.mehmetaslantas.flightdemo.service;

import com.mehmetaslantas.flightdemo.model.Flight;
import reactor.core.publisher.Flux;

public interface FlightService {
    Flux<Flight> getFlights();
}