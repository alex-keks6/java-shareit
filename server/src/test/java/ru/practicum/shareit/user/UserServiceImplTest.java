package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService userService;

    private final List<UserDto> usersRequestDto = List.of(
            UserDto.builder().name("Alex").email("alex123@mail.com").build(),
            UserDto.builder().name("Tony").email("tony321@mail.com").build()
    );

    @Test
    void getAllTest() {
        for (UserDto userDto : usersRequestDto) {
            User user = UserMapper.mapUserDtoToUser(userDto);
            em.persist(user);
        }
        em.flush();

        List<UserDto> users = userService.getAll();

        assertThat(users, hasSize(usersRequestDto.size()));
        for (UserDto userRequestDto : usersRequestDto) {
            assertThat(users, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(userRequestDto.getName())),
                    hasProperty("email", equalTo(userRequestDto.getEmail()))
            )));
        }
    }

    @Test
    void getTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);
        em.flush();

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = query.setParameter("email", user.getEmail())
                .getSingleResult().getId();

        UserDto userDto = userService.get(userId);

        assertThat(userDto.getId(), equalTo(userId));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void addTest() {
        UserDto userDto = usersRequestDto.getFirst();

        userService.add(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);
        em.flush();

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = query.setParameter("email", user.getEmail())
                .getSingleResult().getId();

        UserDto updateUserDto = usersRequestDto.get(1);

        userService.update(updateUserDto, userId);

        query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User updatedUser = query.setParameter("email", updateUserDto.getEmail())
                .getSingleResult();

        assertThat(updatedUser.getId(), equalTo(userId));
        assertThat(updatedUser.getName(), equalTo(updateUserDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updateUserDto.getEmail()));
    }

    @Test
    void removeTest() {
        User user = UserMapper.mapUserDtoToUser(usersRequestDto.getFirst());
        em.persist(user);
        em.flush();

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        Long userId = query.setParameter("email", user.getEmail())
                .getSingleResult().getId();

        userService.remove(userId);

        assertThrows(NoResultException.class, () ->
                em.createQuery("Select u from User u where u.email = :email", User.class)
                        .setParameter("email", user.getEmail())
                        .getSingleResult());
    }
}
