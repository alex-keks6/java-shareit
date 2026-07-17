package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.mapItemDtoToItem(itemDto);

        User user = takeUserById(userId);
        item.setOwner(user);

        if (itemDto.getRequestId() != null) {
            Optional<ItemRequest> optionalRequest = itemRequestRepository.findById(itemDto.getRequestId());
            if (optionalRequest.isEmpty()) {
                throw new DataNotFoundException("Запрос с id = " + itemDto.getRequestId() + " не найден.");
            }
            item.setRequest(optionalRequest.get());
        }

        return ItemMapper.mapItemToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto newItemDto, Long userId, Long itemId) {
        newItemDto.setId(itemId);

        Item oldItem = takeItemById(newItemDto.getId());

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не является владельцем вещи.");
        }

        if (newItemDto.getName() != null && !newItemDto.getName().isBlank()) {
            oldItem.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null && !newItemDto.getDescription().isBlank()) {
            oldItem.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            oldItem.setAvailable(newItemDto.getAvailable());
        }
        return ItemMapper.mapItemToItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemAdvancedDto get(Long itemId) {
        Item item = takeItemById(itemId);

        ItemAdvancedDto itemAdvancedDto = ItemMapper.mapItemToItemAdvancedDto(item);
        itemAdvancedDto.setComments(commentRepository.findAllByItem(item).stream()
                .map(ItemMapper::mapCommentToCommentDto)
                .toList());

        return itemAdvancedDto;
    }

    @Override
    public List<ItemAdvancedDto> getOwnerAll(Long userId) {
        User owner = takeUserById(userId);

        List<ItemAdvancedDto> items = itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::mapItemToItemAdvancedDto)
                .toList();

        List<Booking> bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(owner);

        for (ItemAdvancedDto item : items) {
            setLastAndNextBooking(item, bookings);
        }

        List<Comment> comments = commentRepository.findAllByItemOwner(owner);

        for (ItemAdvancedDto item : items) {
            for (Comment comment : comments) {
                if (comment.getItem().getId().equals(item.getId())) {
                    item.getComments().add(ItemMapper.mapCommentToCommentDto(comment));
                }
            }
        }

        return items;
    }

    @Override
    public List<ItemDto> find(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllByNameOrDescriptionContainingAndAvailableIsTrue(text.toLowerCase()).stream()
                .map(ItemMapper::mapItemToItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        User consumer = takeUserById(userId);
        Item item = takeItemById(itemId);

        Booking booking = bookingRepository.findByBookerAndItemAndEndBefore(consumer, item,
                currentDateTime);
        if (booking == null) {
            throw new ValidationException("Подходящая запись бронирования для создания отзыва не найдена.");
        }

        Comment comment = ItemMapper.mapCommentDtoToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(consumer);
        comment.setCreated(currentDateTime);

        return ItemMapper.mapCommentToCommentDto(commentRepository.save(comment));
    }

    private User takeUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        return optionalUser.get();
    }

    private Item takeItemById(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);

        if (optionalItem.isEmpty()) {
            throw new DataNotFoundException("Вещь с id = " + itemId + " не найдена.");
        }
        return optionalItem.get();
    }

    private void setLastAndNextBooking(ItemAdvancedDto item, List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (booking.getItem().getId().equals(item.getId())) {
                if (booking.getEnd().isBefore(LocalDateTime.now())
                        && (item.getLastBooking() == null
                        || item.getLastBooking().getStart().isBefore(booking.getStart()))) {
                    item.setLastBooking(BookingMapper.mapBookingToBookingStatusDto(booking));
                } else if (booking.getStart().isAfter(LocalDateTime.now())
                        && (item.getNextBooking() == null
                        || item.getNextBooking().getStart().isAfter(booking.getStart()))) {
                    item.setNextBooking(BookingMapper.mapBookingToBookingStatusDto(booking));
                }
            }
        }
    }
}
