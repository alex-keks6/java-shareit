package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.validation.Add;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final BookingServiceImpl service;

    @PostMapping
    public BookingStatusDto add(@Validated(Add.class) @RequestBody BookingDto bookingDto,
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
                                                @RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        return service.getAllForUser(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingStatusDto> getAllForOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                                 @RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        return service.getAllForOwner(state, userId);
    }
}
