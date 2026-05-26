package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("itemStorageInMemory")
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item add(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item get(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getOwnerAll(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Item> find(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }

    @Override
    public Boolean isItemExist(Long itemId) {
        return items.containsKey(itemId);
    }
}
