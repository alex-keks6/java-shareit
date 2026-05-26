package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("userStorageInMemory")
public class UserStorageInMemory implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(Long id) {
        return users.get(id);
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
}
