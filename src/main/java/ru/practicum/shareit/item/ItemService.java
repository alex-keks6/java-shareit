package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item add(Item item, Long userId);

    Item update(Item item, Long userId);

    Item get(Long itemId);

    List<Item> getOwnerAll(Long userId);

    List<Item> find(String text);

    Boolean isItemExist(Long id);
}
