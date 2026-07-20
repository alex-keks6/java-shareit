package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.mapItemRequestDtoToItemRequest(itemRequestDto);
        User user = takeUserById(userId);

        itemRequest.setUser(user);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.mapItemRequestToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestResponseDto> getUserItemRequests(Long userId) {
        isUserExists(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId);
        List<Long> requestsId = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> allResponses = itemRepository.findAllByRequestIdIn(requestsId);

        List<ItemRequestResponseDto> requestsResponses = requests.stream()
                .map(ItemRequestMapper::mapItemRequestToItemRequestResponseDto)
                .toList();

        for (ItemRequestResponseDto requestResponses : requestsResponses) {
            List<ItemResponseDto> responses = new ArrayList<>();
            for (Item response : allResponses) {
                if (response.getRequest().getId().equals(requestResponses.getId())) {
                    responses.add(ItemMapper.mapItemToItemResponseDto(response));
                }
            }
            requestResponses.setItems(responses);
        }

        return requestsResponses;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        return itemRequestRepository.findAllByOrderByCreatedDesc().stream()
                .map(ItemRequestMapper::mapItemRequestToItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestResponseDto getItemRequest(Long requestId) {
        Optional<ItemRequest> optionalRequest = itemRequestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new DataNotFoundException("Запрос с id = " + requestId + " не найден.");
        }
        ItemRequest request = optionalRequest.get();

        ItemRequestResponseDto requestResponse = ItemRequestMapper.mapItemRequestToItemRequestResponseDto(request);
        List<ItemResponseDto> itemResponses = itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::mapItemToItemResponseDto)
                .toList();

        requestResponse.setItems(itemResponses);

        return requestResponse;
    }

    private User takeUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        return optionalUser.get();
    }

    private void isUserExists(Long userId) {
        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            throw new DataNotFoundException("Пользователь с id = " + userId + " не найден.");
        }
    }
}
