package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item add(Item item);

    Item update(Item item);

    Optional<Item> get(Long itemId);

    List<Item> getOwnerAll(Long userId);

    List<Item> find(String text);

    Boolean isItemExist(Long itemId);
}
