package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Override
    public BookingStatusDto add(BookingDto bookingDto, Long userId) {
        User booker = takeUserById(userId);
        Item item = takeItemById(bookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id = " + item.getId() + " недоступна для бронирования.");
        }

        Booking booking = BookingMapper.mapBookingDtoToBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.mapBookingToBookingStatusDto(bookingRepository.save(booking));
    }

    @Override
    public BookingStatusDto confirm(Long bookingId, Boolean approved, Long userId) {
        Booking booking = takeBookingById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь с id = " + userId + " не является владельцем вещи " +
                    "из записи бронирования с id = " + bookingId + ".");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.mapBookingToBookingStatusDto(bookingRepository.save(booking));
    }

    @Override
    public BookingStatusDto get(Long bookingId, Long userId) {
        User user = takeUserById(userId);
        Booking booking = takeBookingById(bookingId);

        if (!(booking.getItem().getOwner().equals(user) || booking.getBooker().equals(user))) {
            throw new ValidationException("Пользователь с id = " + userId + " не является владельцем вещи " +
                    "и не является автором " +
                    "записи бронирования с id = " + bookingId + ".");
        }

        return BookingMapper.mapBookingToBookingStatusDto(booking);
    }

    @Override
    public List<BookingStatusDto> getAllForUser(BookingState state, Long userId, Integer from, Integer size) {
        User booker = takeUserById(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Booking> bookings = bookingRepository.findAllByBookerOrderByStartDesc(booker, page)
                .getContent();

        if (bookings.isEmpty()) {
            throw new DataNotFoundException("У пользователя с id = " + userId + " не найдено бронирований.");
        }

        bookings = filterBookingsByState(bookings, state);

        return bookings.stream()
                .map(BookingMapper::mapBookingToBookingStatusDto)
                .toList();
    }

    @Override
    public List<BookingStatusDto> getAllForOwner(BookingState state, Long userId) {
        User owner = takeUserById(userId);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(owner);
        if (bookings.isEmpty()) {
            throw new DataNotFoundException("У владельца с id = " + userId + " не найдено бронирований на его вещи.");
        }

        bookings = filterBookingsByState(bookings, state);

        return bookings.stream()
                .map(BookingMapper::mapBookingToBookingStatusDto)
                .toList();
    }

    private User takeUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        return optionalUser.get();
    }

    private Booking takeBookingById(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new DataNotFoundException("Бронирование с id = " + bookingId + " не найдено.");
        }
        return optionalBooking.get();
    }

    private Item takeItemById(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new DataNotFoundException("Вещь с id = " + itemId + " не найдена.");
        }
        return optionalItem.get();
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, BookingState state) {
        switch (state) {
            case ALL:
                break;
            case WAITING:
            case REJECTED:
                bookings = bookings.stream()
                        .filter(booking -> booking.getStatus().name().equals(state.name()))
                        .toList();
                break;
            case CURRENT:
                bookings = bookings.stream()
                        .filter(booking -> !booking.getStart().isAfter(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now()))
                        .toList();
                break;
            case PAST:
                bookings = bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                                && booking.getEnd().isBefore(LocalDateTime.now()))
                        .toList();
                break;
            case FUTURE:
                bookings = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now()))
                        .toList();
                break;
            default:
                throw new DataNotFoundException("Функционал с данным параметром state = " + state + " не реализован.");
        }

        return bookings;
    }
}