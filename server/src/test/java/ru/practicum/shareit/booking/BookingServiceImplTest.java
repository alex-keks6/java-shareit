package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private static final LocalDateTime currentDateTime = LocalDateTime.now();

    private final List<BookingDto> incomingBookingsDto = List.of(
            BookingDto.builder().start(currentDateTime.plusHours(1)).end(currentDateTime.plusHours(2)).build(),
            BookingDto.builder().start(currentDateTime.plusDays(1)).end(currentDateTime.plusDays(2)).build()
    );

    private final List<ItemDto> incomingItemsDto = List.of(
            ItemDto.builder().name("Предмет 1").description("Описание предмета 1").available(true).build(),
            ItemDto.builder().name("Предмет 2").description("Описание предмета 2").available(false).build()
    );

    private final List<UserDto> usersRequestDto = List.of(
            UserDto.builder().name("Alex").email("alex123@mail.com").build(),
            UserDto.builder().name("Tony").email("tony321@mail.com").build()
    );

    @Test
    void getAllForUserTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();
        user.setId(userId);

        Item item = ItemMapper.mapItemDtoToItem(incomingItemsDto.getFirst());
        item.setOwner(user);
        em.persist(item);

        TypedQuery<Item> itemQuery = em.createQuery("Select i from Item i where i.description = :description",
                Item.class);
        Long itemId = itemQuery.setParameter("description", item.getDescription())
                .getSingleResult().getId();
        item.setId(itemId);

        for (BookingDto incomingBookingDto : incomingBookingsDto) {
            Booking booking = BookingMapper.mapBookingDtoToBooking(incomingBookingDto);
            booking.setItem(item);
            booking.setBooker(user);
            booking.setStatus(BookingStatus.APPROVED);
            em.persist(booking);
        }
        em.flush();

        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 10;
        List<BookingStatusDto> bookingsStatusDto = bookingService.getAllForUser(state, userId, from, size);

        assertThat(bookingsStatusDto, hasSize(incomingBookingsDto.size()));
        for (BookingDto incomingBookingDto : incomingBookingsDto) {
            assertThat(bookingsStatusDto, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("item", hasProperty("id", equalTo(itemId))),
                    hasProperty("start", equalTo(incomingBookingDto.getStart())),
                    hasProperty("booker", hasProperty("id", equalTo(userId))),
                    hasProperty("status", equalTo(BookingStatus.APPROVED))
            )));
        }
    }
}
