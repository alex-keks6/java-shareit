package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Add;
import ru.practicum.shareit.validation.Update;

import java.util.List;

import static ru.practicum.shareit.item.ItemMapper.map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto add(@Validated(Add.class) @RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = map(itemDto);
        return map(service.add(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated(Update.class) @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId) {
        Item item = map(itemDto);
        item.setId(itemId);
        return map(service.update(item, userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        return map(service.get(itemId));
    }

    @GetMapping
    public List<ItemDto> getOwnerAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getOwnerAll(userId).stream()
                .map(ItemMapper::map)
                .toList();
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text) {
        return service.find(text).stream()
                .map(ItemMapper::map)
                .toList();
    }
}
