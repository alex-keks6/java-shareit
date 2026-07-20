package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemService service;

    @PostMapping
    public ItemDto add(@RequestBody ItemDto itemDto,
                       @RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        return service.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID_REQUEST_HEADER) Long userId,
                          @PathVariable Long itemId) {
        return service.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemAdvancedDto get(@PathVariable Long itemId) {
        return service.get(itemId);
    }

    @GetMapping
    public List<ItemAdvancedDto> getOwnerAll(@RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        return service.getOwnerAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text) {
        return service.find(text);
    }

    // Эндпоинт для работы с комментариями

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader(USER_ID_REQUEST_HEADER) Long userId) {
        return service.addComment(commentDto, itemId, userId);
    }
}
