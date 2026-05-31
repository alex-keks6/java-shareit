package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemMapper {
    public ItemDto map(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .useCount(item.getUseCount())
                .build();
    }

    public Item map(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
