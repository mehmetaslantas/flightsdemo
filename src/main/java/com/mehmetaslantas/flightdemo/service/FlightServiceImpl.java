package com.mehmetaslantas.flightdemo.service;

import com.mehmetaslantas.flightdemo.model.BusinessFlight;
import com.mehmetaslantas.flightdemo.model.CheapFlight;
import com.mehmetaslantas.flightdemo.model.Flight;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class FlightServiceImpl implements FlightService {

    private static final String API_MIME_TYPE = "application/json";
    private static final String API_BASE_URL = "https://obscure-caverns-79008.herokuapp.com";

    private final WebClient webClient;

    public FlightServiceImpl() {
        this.webClient = WebClient.builder()
                .baseUrl(API_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, API_MIME_TYPE)
                .build();
    }

    @Override
    public Flux<Flight> getFlights() {
        var cheapFlights = webClient.get()
                .uri("/cheap")
                .retrieve()
                .bodyToFlux(CheapFlight.class)
                .map(this::ConvertToFlight);

        var businessFlights = webClient.get()
                .uri("/business")
                .retrieve()
                .bodyToFlux(BusinessFlight.class)
                .map(this::ConvertToFlight);

        return Flux.concat(cheapFlights, businessFlights)
                .filter(flight -> new Date().before(flight.getDepartureDate()))
                .sort((o1, o2) -> o1.getDepartureDate().compareTo(o2.getDepartureDate()));
    }

    private Flight ConvertToFlight(CheapFlight cheapFlight) {
        var flight = new Flight();
        flight.setId(cheapFlight.getId() + "");

        flight.setDeparture(cheapFlight.getDeparture());
        flight.setArrival(cheapFlight.getArrival());

        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(cheapFlight.getDepartureTime());
        flight.setDepartureDate(calendar.getTime());

        calendar.setTimeInMillis(cheapFlight.getArrivalTime());
        flight.setArrivalDate(calendar.getTime());

        return flight;
    }

    private Flight ConvertToFlight(BusinessFlight businessFlight) {
        var flight = new Flight();
        flight.setId(businessFlight.getUuid());

        var locationArray = businessFlight.getFlight().split("->");
        var departure = locationArray[0];
        var arrival = locationArray.length > 1 ? locationArray[1] : null;

        flight.setDeparture(departure);
        flight.setArrival(arrival);

        try {
            var dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            flight.setDepartureDate(dateFormatter.parse(businessFlight.getDeparture()));
            flight.setArrivalDate(dateFormatter.parse(businessFlight.getArrival()));
        } catch (ParseException e) {
            // ignored
        }

        return flight;
    }
}

