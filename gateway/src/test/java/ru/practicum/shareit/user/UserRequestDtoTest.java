package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRequestDtoTest {
    private final JacksonTester<UserRequestDto> json;

    @Test
    void testUserRequestDto() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto(
                "Пользователь 1",
                "user1@mail.com"
        );

        JsonContent<UserRequestDto> result = json.write(userRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Пользователь 1");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user1@mail.com");
    }
}
