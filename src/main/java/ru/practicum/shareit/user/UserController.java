package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Add;
import ru.practicum.shareit.validation.Update;

import java.util.List;

import static ru.practicum.shareit.user.UserMapper.map;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping()
    public List<UserDto> getAll() {
        return service.getAll().stream()
                .map(UserMapper::map)
                .toList();
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        return map(service.get(userId));
    }

    @PostMapping
    public UserDto add(@Validated(Add.class) @RequestBody UserDto userDto) {
        User user = UserMapper.map(userDto);
        return map(service.add(user));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable Long userId) {
        User user = UserMapper.map(userDto);
        user.setId(userId);
        return map(service.update(user));
    }

    @DeleteMapping("/{userId}")
    public UserDto remove(@PathVariable Long userId) {
        return map(service.remove(userId));
    }
}
