package com.autoslava.booking.dao;

import com.autoslava.booking.model.Response;
import com.autoslava.booking.model.Trip;
import com.autoslava.booking.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookingDaoTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookingDao bookingDao;

    @Test
    public void getTrips() throws IOException {
        Response response = FileUtil.getObjectFromFile("tripsResponse.json", Response.class);
        ResponseEntity responseEntity = new ResponseEntity(response, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), any())).thenReturn(responseEntity);

        List<Trip> trips = bookingDao.getTrips();
        assertEquals(8, trips.size());
    }
}
