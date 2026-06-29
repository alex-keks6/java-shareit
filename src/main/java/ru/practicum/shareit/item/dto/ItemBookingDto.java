package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    @Builder.Default
    private List<CommentDto> comments = new ArrayList<>();
}
