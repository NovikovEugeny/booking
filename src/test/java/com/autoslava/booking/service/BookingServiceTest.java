package com.autoslava.booking.service;

import com.autoslava.booking.dao.BookingDao;
import com.autoslava.booking.model.BookingInfo;
import com.autoslava.booking.model.Response;
import com.autoslava.booking.model.Trip;
import com.autoslava.booking.util.FileUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookingServiceTest {

    @Mock
    private BookingDao bookingDao;

    @InjectMocks
    private BookingService bookingService;

    @Test
    public void book() throws IOException {
        Response response = FileUtil.getObjectFromFile("tripsResponse.json", Response.class);
        List<Trip> trips = new ArrayList<>(response.getData().getTrips().values()).stream()
                .filter(t -> t.getDate().equals(LocalDate.parse("2000-02-02")))
                .peek(t -> t.setDate(LocalDate.now()))
                .collect(Collectors.toList());

        when(bookingDao.getTrips()).thenReturn(trips);

        bookingService.book();

        ArgumentCaptor<LocalTime> timeArg = ArgumentCaptor.forClass(LocalTime.class);
        verify(bookingDao).book(timeArg.capture(), any(BookingInfo.class));
        assertEquals("19:00", timeArg.getValue().toString());
    }
}
