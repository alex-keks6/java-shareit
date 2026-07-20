package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Add;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotBlank(groups = Add.class)
    private String name;
    @NotBlank(groups = Add.class)
    private String description;
    @NotNull(groups = Add.class)
    private Boolean available;
    private Long requestId;
}
