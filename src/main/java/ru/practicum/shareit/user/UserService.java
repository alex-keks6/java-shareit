package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User get(Long id);

    User add(User user);

    User update(User user);

    User remove(Long id);

    Boolean isUserExist(Long id);
}
