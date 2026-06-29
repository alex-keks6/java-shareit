package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    ItemAdvancedDto get(Long itemId);

    List<ItemAdvancedDto> getOwnerAll(Long userId);

    List<ItemDto> find(String text);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
