package studio.aroundhub.todolistappproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import studio.aroundhub.todolistappproject.dto.AddScheduleRequest;
import studio.aroundhub.todolistappproject.repository.ToDoRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;

@SpringBootTest
@AutoConfigureMockMvc
class ToDoControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ToDoRepository toDoRepository;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        toDoRepository.deleteAll();
    }


    @DisplayName("새로운 일정 추가")
    @Test
    void newSchedule() throws Exception {
        String url = "/Shcedule";
        String email = "java@gmail.com";
        String title = "newSchedule";
        String category = "Game";
        LocalDate schedule = LocalDate.of(2020, 1, 1);
        AddScheduleRequest addScheduleRequest = new AddScheduleRequest(email, title, category, schedule, null);
        final String requestBody = objectMapper.writeValueAsString(addScheduleRequest);
    }

    @Test
    void deleteSchedule() {
    }

    @Test
    void modificationSchedule() {
    }

    @Test
    void getMonthList() {
    }
}