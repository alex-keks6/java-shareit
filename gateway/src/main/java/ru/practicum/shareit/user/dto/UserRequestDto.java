package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.validation.Add;
import ru.practicum.shareit.validation.Update;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @NotBlank(groups = Add.class)
    private String name;

    @NotBlank(groups = Add.class)
    @Email(groups = {Add.class, Update.class})
    private String email;
}
