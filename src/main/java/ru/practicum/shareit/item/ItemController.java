package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public ItemDto get(@PathVariable Long itemId) {
        return service.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getOwnerAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text) {
        return service.find(text);
    }
}
