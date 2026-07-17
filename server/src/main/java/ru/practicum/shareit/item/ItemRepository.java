package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true " +
            "and (lower(it.name) like concat('%', ?1, '%') or lower(it.description) like concat('%', ?1, '%'))")
    List<Item> findAllByNameOrDescriptionContainingAndAvailableIsTrue(String text);

    List<Item> findAllByRequestIdIn(List<Long> requestsId);

    List<Item> findAllByRequestId(Long requestId);
}