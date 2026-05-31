package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAll();

    Optional<User> get(Long id);

    User add(User user);

    User update(User user);

    User remove(Long id);

    Boolean isUserExist(Long id);

    void isEmailExist(String email);
}
