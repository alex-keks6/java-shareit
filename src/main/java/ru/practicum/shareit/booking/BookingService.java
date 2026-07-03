package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {

    BookingStatusDto add(BookingDto bookingDto, Long userId);

    BookingStatusDto confirm(Long bookingId, Boolean approved, Long userId);

    BookingStatusDto get(Long bookingId, Long userId);

    List<BookingStatusDto> getAllForUser(BookingState state, Long userId);

    List<BookingStatusDto> getAllForOwner(BookingState state, Long userId);
}