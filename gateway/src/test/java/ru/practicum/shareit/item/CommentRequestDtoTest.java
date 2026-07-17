package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentRequestDtoTest {
    private final JacksonTester<CommentRequestDto> json;

    @Test
    void testCommentRequestDto() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto(
                "Текст комментария 1"
        );

        JsonContent<CommentRequestDto> result = json.write(commentRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Текст комментария 1");
    }
}
