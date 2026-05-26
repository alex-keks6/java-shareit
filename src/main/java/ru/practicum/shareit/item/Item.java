package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validation.Add;

@Data
@Builder
public class Item {
    private Long id;
    @NotBlank(groups = Add.class)
    private String name;
    @NotBlank(groups = Add.class)
    private String description;
    private User owner;
    @NotNull(groups = Add.class)
    private Boolean available;
    @Builder.Default
    private Long useCount = 0L;
}
