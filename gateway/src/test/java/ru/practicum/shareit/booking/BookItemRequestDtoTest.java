package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookItemRequestDtoTest {
    private final JacksonTester<BookItemRequestDto> json;

    @Test
    void testBookItemRequestDto() throws Exception {
        LocalDateTime currentDateTime = LocalDateTime.now();
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(
                1L,
                currentDateTime.plusHours(1),
                currentDateTime.plusHours(2)
        );

        JsonContent<BookItemRequestDto> result = json.write(bookItemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(currentDateTime.plusHours(1).format(ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(currentDateTime.plusHours(2).format(ISO_LOCAL_DATE_TIME));
    }
}
