package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final BookingService service;

    @PostMapping
    public BookingStatusDto add(@RequestBody BookingDto bookingDto,
                                @RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        return service.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingStatusDto confirm(@PathVariable Long bookingId,
                                    @RequestParam Boolean approved,
                                    @RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        return service.confirm(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingStatusDto get(@PathVariable Long bookingId,
                                @RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        return service.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingStatusDto> getAllForUser(@RequestParam(defaultValue = "ALL") BookingState state,
                                                @RequestHeader(USER_ID_REQUEST_HEADER) Long userId,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllForUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingStatusDto> getAllForOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                                 @RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        return service.getAllForOwner(state, userId);
    }
}
