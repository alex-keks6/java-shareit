package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerOrderByStartDesc(User booker, Pageable page);

    List<Booking> findAllByItemOwnerOrderByStartDesc(User owner);

    Booking findByBookerAndItemAndEndBefore(User booker, Item item, LocalDateTime end);
}