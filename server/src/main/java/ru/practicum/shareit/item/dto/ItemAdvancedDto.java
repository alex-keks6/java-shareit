package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingStatusDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemAdvancedDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingStatusDto lastBooking;
    private BookingStatusDto nextBooking;
    @Builder.Default
    private List<CommentDto> comments = new ArrayList<>();
}