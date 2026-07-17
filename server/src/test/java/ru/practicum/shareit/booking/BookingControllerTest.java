package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    private static final LocalDateTime currentDateTime = LocalDateTime.now();
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private final List<BookingDto> incomingBookingsDto = List.of(
            BookingDto.builder().itemId(1L).start(currentDateTime.plusHours(1)).end(currentDateTime.plusHours(2)).build(),
            BookingDto.builder().itemId(2L).start(currentDateTime.plusDays(1)).end(currentDateTime.plusDays(2)).build()
    );

    private final List<BookingStatusDto> bookingsStatusDto = List.of(
            BookingStatusDto.builder()
                    .id(1L)
                    .item(ItemDto.builder()
                            .id(1L)
                            .name("Предмет 1")
                            .description("Описание предмета 1")
                            .available(true)
                            .build())
                    .start(currentDateTime.plusHours(1))
                    .end(currentDateTime.plusHours(2))
                    .status(BookingStatus.APPROVED)
                    .booker(UserDto.builder()
                            .id(1L)
                            .name("Пользователь 1")
                            .email("user1@mail.com")
                            .build())
                    .build(),
            BookingStatusDto.builder()
                    .id(2L)
                    .item(ItemDto.builder()
                            .id(2L)
                            .name("Предмет 2")
                            .description("Описание предмета 2")
                            .available(true)
                            .build())
                    .start(currentDateTime.plusDays(1))
                    .end(currentDateTime.plusDays(2))
                    .status(BookingStatus.APPROVED)
                    .booker(UserDto.builder()
                            .id(2L)
                            .name("Пользователь 2")
                            .email("user2@mail.com")
                            .build())
                    .build()
    );

    @Test
    void addTest() throws Exception {
        Long userId = 1L;
        BookingDto incomingBookingDto = incomingBookingsDto.getFirst();
        BookingStatusDto bookingStatusDto = bookingsStatusDto.getFirst();

        when(bookingService.add(incomingBookingDto, userId))
                .thenReturn(bookingStatusDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(incomingBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingStatusDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingStatusDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingStatusDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingStatusDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingStatusDto.getItem().getAvailable())))
                .andExpect(jsonPath("$.start", is(bookingStatusDto.getStart().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingStatusDto.getEnd().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingStatusDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingStatusDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingStatusDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingStatusDto.getBooker().getEmail())));
    }

    @Test
    void confirmTest() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;
        BookingStatusDto bookingStatusDto = bookingsStatusDto.getFirst();

        when(bookingService.confirm(bookingId, approved, userId))
                .thenReturn(bookingStatusDto);

        mvc.perform(patch("/bookings/" + bookingId + "?approved=" + approved)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingStatusDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingStatusDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingStatusDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingStatusDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingStatusDto.getItem().getAvailable())))
                .andExpect(jsonPath("$.start", is(bookingStatusDto.getStart().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingStatusDto.getEnd().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingStatusDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingStatusDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingStatusDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingStatusDto.getBooker().getEmail())));
    }

    @Test
    void getTest() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        BookingStatusDto bookingStatusDto = bookingsStatusDto.getFirst();

        when(bookingService.get(bookingId, userId))
                .thenReturn(bookingStatusDto);

        mvc.perform(get("/bookings/" + bookingId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingStatusDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingStatusDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingStatusDto.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingStatusDto.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(bookingStatusDto.getItem().getAvailable())))
                .andExpect(jsonPath("$.start", is(bookingStatusDto.getStart().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingStatusDto.getEnd().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingStatusDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingStatusDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingStatusDto.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingStatusDto.getBooker().getEmail())));
    }

    @Test
    void getAllForUserTest() throws Exception {
        BookingState state = BookingState.ALL;
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<BookingStatusDto> bookingsStatusDto = List.of(this.bookingsStatusDto.getFirst());

        when(bookingService.getAllForUser(state, userId, from, size))
                .thenReturn(bookingsStatusDto);

        mvc.perform(get("/bookings?state=" + state + "&from=" + from + "&size=" + size)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(bookingsStatusDto.size())))
                .andExpect(jsonPath("$.[0].id", is(bookingsStatusDto.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id", is(bookingsStatusDto.getFirst().getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingsStatusDto.getFirst().getItem().getName())))
                .andExpect(jsonPath("$.[0].item.description", is(bookingsStatusDto.getFirst().getItem().getDescription())))
                .andExpect(jsonPath("$.[0].item.available", is(bookingsStatusDto.getFirst().getItem().getAvailable())))
                .andExpect(jsonPath("$.[0].start", is(bookingsStatusDto.getFirst().getStart().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingsStatusDto.getFirst().getEnd().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].status", is(bookingsStatusDto.getFirst().getStatus().name())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingsStatusDto.getFirst().getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingsStatusDto.getFirst().getBooker().getName())))
                .andExpect(jsonPath("$.[0].booker.email", is(bookingsStatusDto.getFirst().getBooker().getEmail())));
    }

    @Test
    void getAllForOwnerTest() throws Exception {
        BookingState state = BookingState.ALL;
        Long userId = 2L;
        List<BookingStatusDto> bookingsStatusDto = List.of(this.bookingsStatusDto.get(1));

        when(bookingService.getAllForOwner(state, userId))
                .thenReturn(bookingsStatusDto);

        mvc.perform(get("/bookings/owner?state=" + state)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(bookingsStatusDto.size())))
                .andExpect(jsonPath("$.[0].id", is(bookingsStatusDto.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id", is(bookingsStatusDto.getFirst().getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingsStatusDto.getFirst().getItem().getName())))
                .andExpect(jsonPath("$.[0].item.description", is(bookingsStatusDto.getFirst().getItem().getDescription())))
                .andExpect(jsonPath("$.[0].item.available", is(bookingsStatusDto.getFirst().getItem().getAvailable())))
                .andExpect(jsonPath("$.[0].start", is(bookingsStatusDto.getFirst().getStart().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingsStatusDto.getFirst().getEnd().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].status", is(bookingsStatusDto.getFirst().getStatus().name())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingsStatusDto.getFirst().getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingsStatusDto.getFirst().getBooker().getName())))
                .andExpect(jsonPath("$.[0].booker.email", is(bookingsStatusDto.getFirst().getBooker().getEmail())));
    }
}
