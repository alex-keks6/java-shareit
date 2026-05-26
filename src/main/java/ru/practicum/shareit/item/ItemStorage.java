package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {
    Item add(Item item);

    Item update(Item item);

    Item get(Long itemId);

    List<Item> getOwnerAll(Long userId);

    List<Item> find(String text);

    Boolean isItemExist(Long itemId);
}
