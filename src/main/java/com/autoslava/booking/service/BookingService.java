package com.autoslava.booking.service;

import com.autoslava.booking.dao.BookingDao;
import com.autoslava.booking.model.BookingInfo;
import com.autoslava.booking.model.Trip;
import com.autoslava.booking.util.Constants;
import com.autoslava.booking.util.FileUtil;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookingService {

    private BookingInfo bookingInfo;

    private Predicate<Trip> timeRangePredicate = t -> t.getDepartureTime().equals(LocalTime.parse(bookingInfo.getStartTime()))
            || t.getDepartureTime().equals(LocalTime.parse(bookingInfo.getEndTime()))
            || (LocalTime.parse(bookingInfo.getStartTime()).isBefore(t.getDepartureTime())
            && LocalTime.parse(bookingInfo.getEndTime()).isAfter(t.getDepartureTime()));

    @Autowired
    private BookingDao bookingDao;

    public BookingService() {
        try {
            bookingInfo = FileUtil.getObjectFromFile("bookingInfo.json", BookingInfo.class);
        } catch (IOException e) {
            log.error("Json file with booking info not found");
            System.exit(1);
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void book() {
        log.info("Searching available trip");

        List<Trip> trips = bookingDao.getTrips();

        Optional<Trip> trip = trips.stream()
                .filter(t -> t.getRouteId().equals(Constants.MINSK_MOGILEV_ROUTE))
                .filter(t -> t.getDate().equals(LocalDate.now()))
                .filter(t -> t.getSeats() >= Integer.parseInt(bookingInfo.getSeats()))
                .filter(timeRangePredicate)
                .min(Comparator.comparing(Trip::getDepartureTime));

        trip.ifPresent(t -> {
            log.info("Corresponding trip was found, departure time: {}", t.getDepartureTime());
            boolean isSuccessful = bookingDao.book(t.getDepartureTime(), bookingInfo);
            if (isSuccessful) {
                log.info("Booking was successful: {}", t);
                System.exit(1);
            }
        });
    }
}
