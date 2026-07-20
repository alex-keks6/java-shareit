package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getUserItemRequests(Long userId);

    List<ItemRequestDto> getAllItemRequests();

    ItemRequestResponseDto getItemRequest(Long requestId);
}
