package ru.practicum.shareit.request;

public class ItemRequestMapper {
    public static ItemRequestDto map(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .request(itemRequest.getRequest())
                .build();
    }

    public static ItemRequest map(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .request(itemRequestDto.getRequest())
                .build();
    }
}
