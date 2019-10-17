package com.autoslava.booking.dao;

import com.autoslava.booking.model.BookingInfo;
import com.autoslava.booking.model.Response;
import com.autoslava.booking.model.ResponseData;
import com.autoslava.booking.model.Trip;
import com.autoslava.booking.util.Constants;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Repository
public class BookingDao {

    private static final String GET_URL = "http://avto-slava.by/timetable/trips/";
    private static final String POST_URL = "http://avto-slava.by/timetable/reservation/";
    private static final String SUCCESSFUL_BOOKING_RESPONSE = "{\"result\": \"success\", \"data\": {\"every_was_fine\": true, \"answer\": {\"email\": null, \"sms\": \"\"}}, \"messages\": []}";
    private static final String NEED_SIDE_PLACE = "Просьба занять одиночное место сбоку";
    private static final String REQUEST_BODY_TEMPLATE =
            "firstname=%s&" +
            "lastname=%s&" +
            "middlename=%s&" +
            "date=%s&" +
            "departure_time=%s&" +
            "phone=%s&" +
            "seats=%s&" +
            "weekday=%s&" +
            "route_id=%s&" +
            "station=2&" +
            "description=%s&" +
            "smscode=&" +
            "needsms=0";

    @Autowired
    private RestTemplate restTemplate;

    public List<Trip> getTrips() {
        ResponseEntity<Response> response = restTemplate.getForEntity(GET_URL, Response.class);
        return Optional.ofNullable(response.getBody())
                .map(Response::getData)
                .map(ResponseData::getTrips)
                .map(Map::values)
                .map(ArrayList::new)
                .orElse(new ArrayList<>());
    }

    public boolean book(LocalTime departureTime, BookingInfo bookingInfo) {
        try {
            String requestBody = String.format(REQUEST_BODY_TEMPLATE,
                    encode(bookingInfo.getFirstName()),
                    encode(bookingInfo.getLastName()),
                    encode(bookingInfo.getMiddleName()),
                    encode(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))),
                    encode(departureTime.toString()),
                    encode(bookingInfo.getPhone()),
                    encode(bookingInfo.getSeats()),
                    encode(getWeekDay()),
                    encode(Integer.toString(Constants.MINSK_MOGILEV_ROUTE)),
                    encode(bookingInfo.isSidePlace() ? NEED_SIDE_PLACE : ""));

            HttpResponse<String> response = Unirest.post(POST_URL)
                    .header("content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(requestBody)
                    .asString();


            return response.getStatus() == 200 && SUCCESSFUL_BOOKING_RESPONSE.equals(response.getBody());
        } catch (UnirestException | UnsupportedEncodingException e) {
            log.error("Error during reservation", e);
            System.exit(1);
            return false;
        }
    }

    private String getWeekDay() {
        int value = LocalDate.now().getDayOfWeek().getValue();
        return value == 7 ? "0" : String.valueOf(value);
    }

    private String encode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
    }
}