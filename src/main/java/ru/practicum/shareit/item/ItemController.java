package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Add;
import ru.practicum.shareit.validation.Update;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto add(@Validated(Add.class) @RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated(Update.class) @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId) {
        return service.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemAdvancedDto get(@PathVariable Long itemId) {
        return service.get(itemId);
    }

    @GetMapping
    public List<ItemAdvancedDto> getOwnerAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getOwnerAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text) {
        return service.find(text);
    }

    // Эндпоинт для работы с комментариями

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Validated(Add.class) @RequestBody CommentDto commentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.addComment(commentDto, itemId, userId);
    }
}
