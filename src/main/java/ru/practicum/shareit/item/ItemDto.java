package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Add;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(groups = Add.class)
    private String name;
    @NotBlank(groups = Add.class)
    private String description;
    @NotNull(groups = Add.class)
    private Boolean available;
    @Builder.Default
    private Long useCount = 0L;
}
