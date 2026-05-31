package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    ItemDto get(Long itemId);

    List<ItemDto> getOwnerAll(Long userId);

    List<ItemDto> find(String text);

    Boolean isItemExist(Long id);
}
