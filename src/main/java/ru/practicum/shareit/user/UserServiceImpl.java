package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.DuplicatedException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    public UserServiceImpl(@Qualifier("userStorageInMemory") UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<User> getAll() {
        return storage.getAll();
    }

    @Override
    public User get(Long id) {
        if (!isUserExist(id)) {
            throw new DataNotFoundException("Пользователь с id = " + id + " не найден.");
        }
        return storage.get(id);
    }

    @Override
    public User add(User user) {
        isEmailExist(user.getEmail());
        return storage.add(user);
    }

    @Override
    public User update(User newUser) {
        User oldUser = get(newUser.getId());

        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            isEmailExist(newUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
        }
        return storage.update(oldUser);
    }

    @Override
    public User remove(Long id) {
        if (!isUserExist(id)) {
            throw new DataNotFoundException("Пользователь для удаления с id = " + id + " не найден.");
        }
        return storage.remove(id);
    }

    @Override
    public Boolean isUserExist(Long id) {
        return storage.isUserExist(id);
    }

    private void isEmailExist(String email) {
        List<User> users = getAll();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                throw new DuplicatedException("Пользователь с email = " + email + " уже существует.");
            }
        }
    }
}
