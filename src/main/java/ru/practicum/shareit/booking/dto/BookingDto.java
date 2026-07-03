package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Add;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;

    @NotNull(groups = Add.class)
    private Long itemId;

    @NotNull(groups = Add.class)
    @FutureOrPresent(groups = Add.class)
    private LocalDateTime start;

    @NotNull(groups = Add.class)
    @FutureOrPresent(groups = Add.class)
    private LocalDateTime end;
}
