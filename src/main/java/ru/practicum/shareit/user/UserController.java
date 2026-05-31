package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Add;
import ru.practicum.shareit.validation.Update;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping()
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        return service.get(userId);
    }

    @PostMapping
    public UserDto add(@Validated(Add.class) @RequestBody UserDto userDto) {
        return service.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable Long userId) {
        return service.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto remove(@PathVariable Long userId) {
        return service.remove(userId);
    }
}
