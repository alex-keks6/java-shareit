package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private UserRepository repository;

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::mapUserToUserDto)
                .toList();
    }

    @Override
    public UserDto get(Long id) {
        Optional<User> optionalUser = repository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Пользователь с id = " + id + " не найден.");
        }
        return UserMapper.mapUserToUserDto(optionalUser.get());
    }

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.mapUserDtoToUser(userDto);

        return UserMapper.mapUserToUserDto(repository.save(user));
    }

    @Transactional
    @Override
    public UserDto update(UserDto newUserDto, Long userId) {
        newUserDto.setId(userId);

        Optional<User> optionalOldUser = repository.findById(newUserDto.getId());
        if (optionalOldUser.isEmpty()) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        User oldUser = optionalOldUser.get();

        if (newUserDto.getName() != null && !newUserDto.getName().isBlank()) {
            oldUser.setName(newUserDto.getName());
        }
        // проверка на email выполняется в UserDto с помощью аннотации
        if (newUserDto.getEmail() != null && !newUserDto.getEmail().isBlank()) {
            oldUser.setEmail(newUserDto.getEmail());
        }
        return UserMapper.mapUserToUserDto(repository.save(oldUser));
    }

    @Transactional
    @Override
    public UserDto remove(Long id) {
        Optional<User> optionalUser = repository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Пользователь для удаления с id = " + id + " не найден.");
        }
        User user = optionalUser.get();
        repository.deleteById(id);

        return UserMapper.mapUserToUserDto(user);
    }
}
