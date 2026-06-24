package com.revature.ers.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tracer for the Request slice: controller -> service -> Spring Data repo ->
 * Hibernate (eager graph) -> seeded PostgreSQL -> JSON.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAll_serializesGraphAndHidesPassword() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").exists())
                // nested entity comes back through the graph...
                .andExpect(jsonPath("$[0].requestStatus.status").exists())
                // ...but the requester's password never does (@JsonIgnore carried over)
                .andExpect(jsonPath("$[0].requester.password").doesNotExist());
    }

    @Test
    void getById_returnsSeededRequestOne() throws Exception {
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(1))
                .andExpect(jsonPath("$.requestedEvent").value("Anime Convention"))
                .andExpect(jsonPath("$.requester.userId").value(2))
                .andExpect(jsonPath("$.requestStatus.statusId").value(2));
    }

    @Test
    void getByRequester_filtersByUser() throws Exception {
        mockMvc.perform(get("/requests/requester/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requester.userId").value(2));
    }
}
