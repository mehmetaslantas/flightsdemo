package com.mehmetaslantas.flightdemo.controller;

import com.mehmetaslantas.flightdemo.model.Flight;
import com.mehmetaslantas.flightdemo.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/")
public class FlightController {

    private FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @RequestMapping(value = "/flights", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Flux<Flight>> getFlights() {
        var flights = flightService.getFlights();
        HttpStatus status = flights != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(flights, status);
    }
}