package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Item;

import java.time.LocalDate;

@Data
@Builder
public class BookingDto {
    private Long id;
    private Item item;
    private LocalDate date;
}
