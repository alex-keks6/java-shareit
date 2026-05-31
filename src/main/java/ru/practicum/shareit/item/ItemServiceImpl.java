package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.item.ItemMapper.map;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(@Qualifier("itemStorageInMemory") ItemStorage itemStorage,
                           @Qualifier("userStorageInMemory") UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        Item item = map(itemDto);
        Optional<User> optionalUser = userStorage.get(userId);

        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        item.setOwner(optionalUser.get());
        return map(itemStorage.add(item));
    }

    @Override
    public ItemDto update(ItemDto newItemDto, Long userId, Long itemId) {
        newItemDto.setId(itemId);

        Optional<Item> optionalOldItem = itemStorage.get(newItemDto.getId());
        if (optionalOldItem.isEmpty()) {
            throw new DataNotFoundException("Вещь с id = " + itemId + " не найдена.");
        }

        Item oldItem = optionalOldItem.get();

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не является владельцем вещи.");
        }

        if (newItemDto.getName() != null && !newItemDto.getName().isBlank()) {
            oldItem.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null && !newItemDto.getDescription().isBlank()) {
            oldItem.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            oldItem.setAvailable(newItemDto.getAvailable());
        }
        return map(itemStorage.update(oldItem));
    }

    @Override
    public ItemDto get(Long itemId) {
        Optional<Item> optionalItem = itemStorage.get(itemId);

        if (optionalItem.isEmpty()) {
            throw new DataNotFoundException("Вещь с id = " + itemId + " не найдена.");
        }
        return map(optionalItem.get());
    }

    @Override
    public List<ItemDto> getOwnerAll(Long userId) {
        if (!userStorage.isUserExist(userId)) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        return itemStorage.getOwnerAll(userId).stream()
                .map(ItemMapper::map)
                .toList();
    }

    @Override
    public List<ItemDto> find(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemStorage.find(text.toLowerCase()).stream()
                .map(ItemMapper::map)
                .toList();
    }

    @Override
    public Boolean isItemExist(Long id) {
        return itemStorage.isItemExist(id);
    }
}
