package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
public class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemService itemService;
    private static final LocalDateTime currentDateTime = LocalDateTime.now();

    private final List<ItemDto> incomingItemsDto = List.of(
            ItemDto.builder().name("Предмет 1").description("Описание предмета 1").available(true).build(),
            ItemDto.builder().name("Предмет 2").description("Описание предмета 2").available(false).build()
    );

    private final List<UserDto> usersRequestDto = List.of(
            UserDto.builder().name("Alex").email("alex123@mail.com").build(),
            UserDto.builder().name("Tony").email("tony321@mail.com").build()
    );

    private final CommentDto incomingCommentDto = CommentDto.builder()
            .text("Комментарий 1")
            .build();

    private final BookingDto incomingBookingDto = BookingDto.builder().itemId(1L)
            .start(currentDateTime.minusHours(2))
            .end(currentDateTime.minusHours(1)).build();

    @Test
    void addTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();

        ItemDto incomingItemDto = incomingItemsDto.getFirst();

        itemService.add(incomingItemDto, userId);

        TypedQuery<Item> itemQuery = em.createQuery(
                "Select t from Item t where t.description = :description", Item.class);
        Item item = itemQuery.setParameter("description", incomingItemDto.getDescription())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(incomingItemDto.getName()));
        assertThat(item.getDescription(), equalTo(incomingItemDto.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(userId));
        assertThat(item.getAvailable(), equalTo(incomingItemDto.getAvailable()));
    }

    @Test
    void updateTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();
        user.setId(userId);

        Item item = ItemMapper.mapItemDtoToItem(incomingItemsDto.getFirst());
        item.setOwner(user);
        em.persist(item);
        em.flush();

        TypedQuery<Item> itemIdQuery = em.createQuery("Select i from Item i where i.description = :description",
                Item.class);
        Long itemId = itemIdQuery.setParameter("description", item.getDescription())
                .getSingleResult().getId();

        ItemDto updateItemDto = incomingItemsDto.get(1);

        itemService.update(updateItemDto, userId, itemId);

        TypedQuery<Item> itemQuery = em.createQuery("Select i from Item i where i.description = :description",
                Item.class);
        Item updatedItem = itemQuery.setParameter("description", updateItemDto.getDescription())
                .getSingleResult();

        assertThat(updatedItem.getId(), equalTo(itemId));
        assertThat(updatedItem.getName(), equalTo(item.getName()));
        assertThat(updatedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(updatedItem.getOwner().getId(), equalTo(userId));
        assertThat(updatedItem.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void getTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();
        user.setId(userId);

        Item item = ItemMapper.mapItemDtoToItem(incomingItemsDto.getFirst());
        item.setOwner(user);
        em.persist(item);

        TypedQuery<Item> itemQuery = em.createQuery(
                "Select t from Item t where t.description = :description", Item.class);
        Long itemId = itemQuery.setParameter("description", item.getDescription())
                .getSingleResult().getId();
        em.flush();

        ItemAdvancedDto itemAdvancedDto = itemService.get(itemId);

        assertThat(itemAdvancedDto.getId(), notNullValue());
        assertThat(itemAdvancedDto.getName(), equalTo(item.getName()));
        assertThat(itemAdvancedDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemAdvancedDto.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void getOwnerAllTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();
        user.setId(userId);

        for (ItemDto incomingItemDto : incomingItemsDto) {
            Item item = ItemMapper.mapItemDtoToItem(incomingItemDto);
            item.setOwner(user);
            em.persist(item);
        }
        em.flush();

        List<ItemAdvancedDto> itemsAdvancedDto = itemService.getOwnerAll(userId);

        assertThat(itemsAdvancedDto, hasSize(incomingItemsDto.size()));
        for (ItemDto userRequestDto : incomingItemsDto) {
            assertThat(itemsAdvancedDto, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(userRequestDto.getName())),
                    hasProperty("description", equalTo(userRequestDto.getDescription())),
                    hasProperty("available", equalTo(userRequestDto.getAvailable()))
            )));
        }
    }

    @Test
    void findTest() {
        String text = "предмет";

        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();
        user.setId(userId);

        List<ItemDto> incomingAvailableItemsDto = incomingItemsDto.stream()
                .filter(ItemDto::getAvailable)
                .toList();

        for (ItemDto incomingItemDto : incomingAvailableItemsDto) {
            Item item = ItemMapper.mapItemDtoToItem(incomingItemDto);
            item.setOwner(user);
            em.persist(item);
        }
        em.flush();

        List<ItemDto> itemsDto = itemService.find(text);

        assertThat(itemsDto, hasSize(incomingAvailableItemsDto.size()));
        for (ItemDto userRequestDto : incomingAvailableItemsDto) {
            assertThat(itemsDto, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(userRequestDto.getName())),
                    hasProperty("description", equalTo(userRequestDto.getDescription())),
                    hasProperty("available", equalTo(userRequestDto.getAvailable()))
            )));
        }
    }

    @Test
    void addCommentTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();
        user.setId(userId);

        Item item = ItemMapper.mapItemDtoToItem(incomingItemsDto.getFirst());
        item.setOwner(user);
        em.persist(item);

        TypedQuery<Item> itemIdQuery = em.createQuery("Select i from Item i where i.description = :description",
                Item.class);
        Long itemId = itemIdQuery.setParameter("description", item.getDescription())
                .getSingleResult().getId();
        item.setId(itemId);

        Booking booking = BookingMapper.mapBookingDtoToBooking(incomingBookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        itemService.addComment(incomingCommentDto, itemId, userId);

        TypedQuery<Comment> commentQuery = em.createQuery("Select c from Comment c where c.text = :text",
                Comment.class);
        Comment comment = commentQuery.setParameter("text", incomingCommentDto.getText())
                .getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(incomingCommentDto.getText()));
        assertThat(comment.getItem().getId(), equalTo(itemId));
        assertThat(comment.getAuthor().getId(), equalTo(userId));
        assertThat(comment.getCreated(), notNullValue());
    }
}
