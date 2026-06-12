package com.people.hub.leavemanagement.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayDto {

    private LocalDate holidayDate;

    private Integer year;

    private String holidayName;

    private Boolean optionalHoliday;
}
