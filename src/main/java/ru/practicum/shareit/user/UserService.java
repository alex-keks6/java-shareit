package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto get(Long id);

    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);

    UserDto remove(Long id);

    Boolean isUserExist(Long id);
}
