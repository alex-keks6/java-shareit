package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Add;
import ru.practicum.shareit.validation.Update;

@Data
@Builder
public class User {
    private Long id;
    @NotBlank(groups = Add.class)
    private String name;
    @NotBlank(groups = Add.class)
    @Email(groups = {Add.class, Update.class})
    private String email;
    @Builder.Default
    private Long itemCount = 0L;

    public void addItemCount() {
        itemCount++;
    }
}
