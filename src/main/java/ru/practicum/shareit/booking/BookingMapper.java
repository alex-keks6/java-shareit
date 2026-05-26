package ru.practicum.shareit.booking;

public class BookingMapper {
    public static BookingDto map(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .date(booking.getDate())
                .build();
    }

    public static Booking map(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .item(bookingDto.getItem())
                .date(bookingDto.getDate())
                .build();
    }
}
