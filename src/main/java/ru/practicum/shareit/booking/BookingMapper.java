package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BookingMapper {
    public BookingDto map(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .date(booking.getDate())
                .build();
    }

    public Booking map(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .item(bookingDto.getItem())
                .date(bookingDto.getDate())
                .build();
    }
}
