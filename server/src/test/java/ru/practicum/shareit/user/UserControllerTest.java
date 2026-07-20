package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private final List<UserDto> usersRequestDto = List.of(
            UserDto.builder().name("Alex").email("alex123@mail.com").build(),
            UserDto.builder().name("Tony").email("tony321@mail.com").build()
    );

    private final List<UserDto> usersDto = List.of(
            UserDto.builder().id(1L).name("Alex").email("alex123@mail.com").build(),
            UserDto.builder().id(2L).name("Tony").email("tony321@mail.com").build()
    );

    @Test
    void getAllTest() throws Exception {
        when(userService.getAll())
                .thenReturn(usersDto);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(usersDto.size())))
                .andExpect(jsonPath("$.[0].id", is(usersDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(usersDto.get(0).getName())))
                .andExpect(jsonPath("$.[0].email", is(usersDto.get(0).getEmail())))
                .andExpect(jsonPath("$.[1].id", is(usersDto.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(usersDto.get(1).getName())))
                .andExpect(jsonPath("$.[1].email", is(usersDto.get(1).getEmail())));
    }

    @Test
    void getTest() throws Exception {
        UserDto userDto = usersDto.getFirst();

        when(userService.get(userDto.getId()))
                .thenReturn(userDto);

        mvc.perform(get("/users/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void addTest() throws Exception {
        UserDto userRequestDto = usersRequestDto.getFirst();
        UserDto userDto = usersDto.getFirst();

        when(userService.add(userRequestDto))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateTest() throws Exception {
        UserDto userRequestDto = usersRequestDto.getFirst();
        UserDto userDto = usersDto.getFirst();

        when(userService.update(userRequestDto, userDto.getId()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void removeTest() throws Exception {
        UserDto userDto = usersDto.getFirst();

        when(userService.remove(userDto.getId()))
                .thenReturn(userDto);

        mvc.perform(delete("/users/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }
}
