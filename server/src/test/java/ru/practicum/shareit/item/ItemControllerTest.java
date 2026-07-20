package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAdvancedDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private static final LocalDateTime currentDateTime = LocalDateTime.now();
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private final List<ItemDto> incomingItemsDto = List.of(
            ItemDto.builder().name("Предмет 1").description("Описание предмета 1").available(true).build(),
            ItemDto.builder().name("Предмет 2").description("Описание предмета 2").available(false).build()
    );

    private final List<ItemDto> itemsDto = List.of(
            ItemDto.builder().id(1L).name("Предмет 1").description("Описание предмета 1").available(true).build(),
            ItemDto.builder().id(2L).name("Предмет 2").description("Описание предмета 2").available(false).build()
    );

    private final List<ItemAdvancedDto> itemsAdvancedDto = List.of(
            ItemAdvancedDto.builder().id(1L).name("Хороший предмет 1").description("Подробное описание предмета 1")
                    .available(true).build(),
            ItemAdvancedDto.builder().id(2L).name("Хороший предмет 2").description("Подробное описание предмета 2")
                    .available(false).build()
    );

    private final CommentDto incomingCommentDto = CommentDto.builder()
            .text("Комментарий 1")
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("Комментарий 1")
            .authorName("Автор 1")
            .created(currentDateTime)
            .build();

    @Test
    void addTest() throws Exception {
        Long userId = 1L;
        ItemDto incomingItemDto = incomingItemsDto.getFirst();
        ItemDto itemDto = itemsDto.getFirst();

        when(itemService.add(incomingItemDto, userId))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(incomingItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void updateTest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto incomingItemDto = incomingItemsDto.getFirst();
        ItemDto itemDto = itemsDto.getFirst();

        when(itemService.update(incomingItemDto, userId, itemId))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/" + itemId)
                        .content(mapper.writeValueAsString(incomingItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getTest() throws Exception {
        Long itemId = 1L;
        ItemAdvancedDto itemAdvancedDto = itemsAdvancedDto.getFirst();

        when(itemService.get(itemId))
                .thenReturn(itemAdvancedDto);

        mvc.perform(get("/items/" + itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemAdvancedDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemAdvancedDto.getName())))
                .andExpect(jsonPath("$.description", is(itemAdvancedDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemAdvancedDto.getAvailable())));
    }

    @Test
    void getOwnerAllTest() throws Exception {
        Long userId = 1L;

        when(itemService.getOwnerAll(userId))
                .thenReturn(itemsAdvancedDto);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(itemsAdvancedDto.size())))
                .andExpect(jsonPath("$.[0].id", is(itemsAdvancedDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemsAdvancedDto.get(0).getName())))
                .andExpect(jsonPath("$.[0].description", is(itemsAdvancedDto.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemsAdvancedDto.get(0).getAvailable())))

                .andExpect(jsonPath("$.[1].id", is(itemsAdvancedDto.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(itemsAdvancedDto.get(1).getName())))
                .andExpect(jsonPath("$.[1].description", is(itemsAdvancedDto.get(1).getDescription())))
                .andExpect(jsonPath("$.[1].available", is(itemsAdvancedDto.get(1).getAvailable())));
    }

    @Test
    void findTest() throws Exception {
        String text = "предмет";
        List<ItemDto> availableItemsDto = List.of(itemsDto.getFirst());

        when(itemService.find(text))
                .thenReturn(availableItemsDto);

        mvc.perform(get("/items/search?text=" + text)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(availableItemsDto.size())))
                .andExpect(jsonPath("$.[0].id", is(availableItemsDto.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(availableItemsDto.getFirst().getName())))
                .andExpect(jsonPath("$.[0].description", is(availableItemsDto.getFirst().getDescription())))
                .andExpect(jsonPath("$.[0].available", is(availableItemsDto.getFirst().getAvailable())));
    }

    @Test
    void addCommentTest() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        when(itemService.addComment(incomingCommentDto, itemId, userId))
                .thenReturn(commentDto);

        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(incomingCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(ISO_LOCAL_DATE_TIME))));
    }
}
