package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
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
public class ItemRequestServiceImplTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final LocalDateTime currentDateTime = LocalDateTime.now();

    private final List<ItemRequestDto> incomingRequestsDto = List.of(
            ItemRequestDto.builder().description("Запрос 1").build(),
            ItemRequestDto.builder().description("Запрос 2").build()
    );

    private final List<UserDto> usersRequestDto = List.of(
            UserDto.builder().name("Alex").email("alex123@mail.com").build(),
            UserDto.builder().name("Tony").email("tony321@mail.com").build()
    );

    @Test
    void addItemRequestTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();

        ItemRequestDto incomingRequestDto = incomingRequestsDto.getFirst();

        itemRequestService.addItemRequest(userId, incomingRequestDto);

        TypedQuery<ItemRequest> requestQuery = em.createQuery(
                "Select it from ItemRequest it where it.description = :description", ItemRequest.class);
        ItemRequest request = requestQuery.setParameter("description", incomingRequestDto.getDescription())
                .getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getUser().getId(), equalTo(userId));
        assertThat(request.getDescription(), equalTo(incomingRequestDto.getDescription()));
        assertThat(request.getCreated(), notNullValue());
    }

    @Test
    void getUserItemRequestsTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();
        user.setId(userId);

        for (ItemRequestDto requestDto : incomingRequestsDto) {
            ItemRequest request = ItemRequestMapper.mapItemRequestDtoToItemRequest(requestDto);
            request.setUser(user);
            request.setCreated(currentDateTime);
            em.persist(request);
        }
        em.flush();

        List<ItemRequestResponseDto> requestsResponsesDto = itemRequestService.getUserItemRequests(userId);

        assertThat(requestsResponsesDto, hasSize(incomingRequestsDto.size()));
        for (ItemRequestDto itemRequestDto : incomingRequestsDto) {
            assertThat(requestsResponsesDto, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemRequestDto.getDescription())),
                    hasProperty("created", equalTo(currentDateTime))
            )));
        }
    }

    @Test
    void getAllItemRequestsTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();
        user.setId(userId);

        for (ItemRequestDto requestDto : incomingRequestsDto) {
            ItemRequest request = ItemRequestMapper.mapItemRequestDtoToItemRequest(requestDto);
            request.setUser(user);
            request.setCreated(currentDateTime);
            em.persist(request);
        }
        em.flush();

        List<ItemRequestDto> requestsDto = itemRequestService.getAllItemRequests();

        assertThat(requestsDto, hasSize(incomingRequestsDto.size()));
        for (ItemRequestDto itemRequestDto : incomingRequestsDto) {
            assertThat(requestsDto, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemRequestDto.getDescription())),
                    hasProperty("created", equalTo(currentDateTime))
            )));
        }
    }

    @Test
    void getItemRequestTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);
        em.flush();

        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = userQuery.setParameter("email", user.getEmail())
                .getSingleResult().getId();
        user.setId(userId);

        ItemRequest request = ItemRequestMapper.mapItemRequestDtoToItemRequest(incomingRequestsDto.getFirst());
        request.setUser(user);
        request.setCreated(currentDateTime);
        em.persist(request);
        em.flush();

        TypedQuery<ItemRequest> requestQuery = em.createQuery(
                "Select it from ItemRequest it where it.description = :description", ItemRequest.class);
        Long requestId = requestQuery.setParameter("description", request.getDescription())
                .getSingleResult().getId();

        ItemRequestResponseDto requestResponseDto = itemRequestService.getItemRequest(requestId);

        assertThat(requestResponseDto.getId(), equalTo(requestId));
        assertThat(requestResponseDto.getDescription(), equalTo(request.getDescription()));
        assertThat(requestResponseDto.getCreated(), equalTo(currentDateTime));
    }
}
