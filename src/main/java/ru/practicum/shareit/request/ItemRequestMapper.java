package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDto map(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .request(itemRequest.getRequest())
                .build();
    }

    public ItemRequest map(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .request(itemRequestDto.getRequest())
                .build();
    }
}
