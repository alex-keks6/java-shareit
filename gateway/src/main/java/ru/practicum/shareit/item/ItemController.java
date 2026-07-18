package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.validation.Add;
import ru.practicum.shareit.validation.Update;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@Positive @RequestHeader(USER_ID_REQUEST_HEADER) long userId,
                                          @Validated(Add.class) @RequestBody ItemRequestDto itemDto
    ) {
        log.info("Post item {} with userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader(USER_ID_REQUEST_HEADER) long userId,
                                             @Validated(Update.class) @RequestBody ItemRequestDto itemDto,
                                             @Positive @PathVariable Long itemId) {
        log.info("Patch item {} with userId={}", itemDto, userId);
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@Positive @PathVariable long itemId) {
        log.info("Get item with itemId={}", itemId);
        return itemClient.getItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@Positive @RequestHeader(USER_ID_REQUEST_HEADER) long userId) {
        log.info("Get owner items with userId={}", userId);
        return itemClient.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItems(@NotBlank @RequestParam String text) {
        log.info("Get items by text={}", text);
        return itemClient.findItems(text);
    }

    // Эндпоинт для работы с комментариями

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @RequestHeader(USER_ID_REQUEST_HEADER) long userId,
                                             @Validated(Add.class) @RequestBody CommentRequestDto commentDto,
                                             @Positive @PathVariable Long itemId
    ) {
        log.info("Post comment {}, userId={}, itemId={}", commentDto, userId, itemId);
        return itemClient.addComment(userId, commentDto, itemId);
    }
}
