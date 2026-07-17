package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private static final LocalDateTime currentDateTime = LocalDateTime.now();
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private final List<ItemRequestDto> incomingRequestsDto = List.of(
            ItemRequestDto.builder().description("Request 1").build(),
            ItemRequestDto.builder().description("Request 2").build()
    );

    private final List<ItemRequestDto> requestsDto = List.of(
            ItemRequestDto.builder().id(1L).description("Request 1").created(currentDateTime).build(),
            ItemRequestDto.builder().id(2L).description("Request 2").created(currentDateTime.minusDays(1)).build()
    );

    private final List<ItemResponseDto> responsesDto = List.of(
            ItemResponseDto.builder().id(1L).name("Item 1").userId(1L).build(),
            ItemResponseDto.builder().id(2L).name("Item 2").userId(2L).build(),
            ItemResponseDto.builder().id(3L).name("Item 3").userId(3L).build()
    );

    private final List<ItemRequestResponseDto> requestsResponsesDto = List.of(
            ItemRequestResponseDto.builder().id(1L).description("Request 1").created(currentDateTime)
                    .items(List.of(responsesDto.get(0), responsesDto.get(1))).build(),
            ItemRequestResponseDto.builder().id(2L).description("Request 2").created(currentDateTime.minusDays(1))
                    .items(List.of(responsesDto.get(1), responsesDto.get(2))).build()
    );

    @Test
    void addItemRequestTest() throws Exception {
        Long userId = 1L;
        ItemRequestDto incomingRequestDto = incomingRequestsDto.getFirst();
        ItemRequestDto requestDto = requestsDto.getFirst();

        when(itemRequestService.addItemRequest(userId, incomingRequestDto))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(incomingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().format(ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void getUserItemRequestsTest() throws Exception {
        Long userId = 1L;

        when(itemRequestService.getUserItemRequests(userId))
                .thenReturn(requestsResponsesDto);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(requestsResponsesDto.size())))
                .andExpect(jsonPath("$.[0].id", is(requestsResponsesDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestsResponsesDto.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].created", is(requestsResponsesDto.get(0).getCreated().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].items.length()", is(requestsResponsesDto.get(0).getItems().size())))
                .andExpect(jsonPath("$.[0].items[0].id", is(requestsResponsesDto.get(0).getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].items[0].name", is(requestsResponsesDto.get(0).getItems().get(0).getName())))
                .andExpect(jsonPath("$.[0].items[0].userId", is(requestsResponsesDto.get(0).getItems().get(0).getUserId()), Long.class))

                .andExpect(jsonPath("$.[1].id", is(requestsResponsesDto.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(requestsResponsesDto.get(1).getDescription())))
                .andExpect(jsonPath("$.[1].created", is(requestsResponsesDto.get(1).getCreated().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[1].items.length()", is(requestsResponsesDto.get(1).getItems().size())))
                .andExpect(jsonPath("$.[1].items[1].id", is(requestsResponsesDto.get(1).getItems().get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].items[1].name", is(requestsResponsesDto.get(1).getItems().get(1).getName())))
                .andExpect(jsonPath("$.[1].items[1].userId", is(requestsResponsesDto.get(1).getItems().get(1).getUserId()), Long.class));
    }

    @Test
    void getAllItemRequestsTest() throws Exception {
        when(itemRequestService.getAllItemRequests())
                .thenReturn(requestsDto);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(requestsDto.size())))
                .andExpect(jsonPath("$.[0].id", is(requestsDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestsDto.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].created", is(requestsDto.get(0).getCreated().format(ISO_LOCAL_DATE_TIME))))

                .andExpect(jsonPath("$.[1].id", is(requestsDto.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(requestsDto.get(1).getDescription())))
                .andExpect(jsonPath("$.[1].created", is(requestsDto.get(1).getCreated().format(ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void getItemRequestTest() throws Exception {
        Long requestId = 1L;

        when(itemRequestService.getItemRequest(requestId))
                .thenReturn(requestsResponsesDto.getFirst());

        mvc.perform(get("/requests/" + requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestsResponsesDto.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestsResponsesDto.getFirst().getDescription())))
                .andExpect(jsonPath("$.created", is(requestsResponsesDto.getFirst().getCreated().format(ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.items.length()", is(requestsResponsesDto.getFirst().getItems().size())))
                .andExpect(jsonPath("$.items[0].id", is(requestsResponsesDto.getFirst().getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(requestsResponsesDto.getFirst().getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].userId", is(requestsResponsesDto.getFirst().getItems().get(0).getUserId()), Long.class))
                .andExpect(jsonPath("$.items[1].id", is(requestsResponsesDto.getFirst().getItems().get(1).getId()), Long.class))
                .andExpect(jsonPath("$.items[1].name", is(requestsResponsesDto.getFirst().getItems().get(1).getName())))
                .andExpect(jsonPath("$.items[1].userId", is(requestsResponsesDto.getFirst().getItems().get(1).getUserId()), Long.class));
    }
}
