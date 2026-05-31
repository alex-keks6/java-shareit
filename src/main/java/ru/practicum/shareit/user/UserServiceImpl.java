package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.user.UserMapper.map;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    public UserServiceImpl(@Qualifier("userStorageInMemory") UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<UserDto> getAll() {
        return storage.getAll().stream()
                .map(UserMapper::map)
                .toList();
    }

    @Override
    public UserDto get(Long id) {
        Optional<User> optionalUser = storage.get(id);

        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Пользователь с id = " + id + " не найден.");
        }
        return map(optionalUser.get());
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.map(userDto);

        storage.isEmailExist(user.getEmail());
        return map(storage.add(user));
    }

    @Override
    public UserDto update(UserDto newUserDto, Long userId) {
        newUserDto.setId(userId);

        Optional<User> optionalOldUser = storage.get(newUserDto.getId());
        if (optionalOldUser.isEmpty()) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не найден.");
        }

        User oldUser = optionalOldUser.get();

        if (newUserDto.getName() != null && !newUserDto.getName().isBlank()) {
            oldUser.setName(newUserDto.getName());
        }
        // проверка на email выполняется в UserDto с помощью аннотации
        if (newUserDto.getEmail() != null && !newUserDto.getEmail().isBlank()) {
            storage.isEmailExist(newUserDto.getEmail());
            oldUser.setEmail(newUserDto.getEmail());
        }
        return map(storage.update(oldUser));
    }

    @Override
    public UserDto remove(Long id) {
        if (!isUserExist(id)) {
            throw new DataNotFoundException("Пользователь для удаления с id = " + id + " не найден.");
        }
        return map(storage.remove(id));
    }

    @Override
    public Boolean isUserExist(Long id) {
        return storage.isUserExist(id);
    }
}
