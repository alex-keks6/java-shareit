package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedException;

import java.util.*;

@Repository("userStorageInMemory")
public class UserStorageInMemory implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User add(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User remove(Long id) {
        User user = users.get(id);
        users.remove(id);
        return user;
    }

    @Override
    public Boolean isUserExist(Long id) {
        return users.containsKey(id);
    }

    @Override
    public void isEmailExist(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                throw new DuplicatedException("Пользователь с email = " + email + " уже существует.");
            }
        }
    }
}
