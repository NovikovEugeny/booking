package com.autoslava.booking.model;

import lombok.Data;

@Data
public class BookingInfo {
    private String firstName;
    private String lastName;
    private String middleName;
    private String startTime;
    private String endTime;
    private String phone;
    private String seats;
    private boolean sidePlace;
}
