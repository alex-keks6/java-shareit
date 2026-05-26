package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserServiceImpl userService;

    public ItemServiceImpl(@Qualifier("itemStorageInMemory") ItemStorage itemStorage,
                           UserServiceImpl userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public Item add(Item item, Long userId) {
        if (!userService.isUserExist(userId)) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        item.setOwner(userService.get(userId));
        userService.get(userId).addItemCount();
        return itemStorage.add(item);
    }

    @Override
    public Item update(Item newItem, Long userId) {
        Item oldItem = get(newItem.getId());

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не является владельцем вещи.");
        }

        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return itemStorage.update(oldItem);
    }

    @Override
    public Item get(Long itemId) {
        if (!isItemExist(itemId)) {
            throw new DataNotFoundException("Вещь с id = " + itemId + " не найдена.");
        }
        return itemStorage.get(itemId);
    }

    @Override
    public List<Item> getOwnerAll(Long userId) {
        if (!userService.isUserExist(userId)) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        return itemStorage.getOwnerAll(userId);
    }

    @Override
    public List<Item> find(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemStorage.find(text);
    }

    @Override
    public Boolean isItemExist(Long id) {
        return itemStorage.isItemExist(id);
    }
}
