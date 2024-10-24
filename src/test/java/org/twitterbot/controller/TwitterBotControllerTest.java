package org.twitterbot.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.twitterbot.service.TwitterBotService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestExecutionListeners(MockitoTestExecutionListener.class)
class TwitterBotControllerTest {

    @InjectMocks
    private TwitterBotController twitterBotController;

    @Mock
    private TwitterBotService twitterBotService;

    private MockMvc mockMvc;

    @Test
    void testTriggerSearchAndReplyWithGif() throws Exception {
        String keyword = "test";
        mockMvc = MockMvcBuilders.standaloneSetup(twitterBotController).build();

        mockMvc.perform(get("/trigger-search-and-reply")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bot triggered for keyword: " + keyword)));

        verify(twitterBotService).searchAndReplyWithGif(keyword);
    }

    @Test
    void testTriggerSearchAndReplyWithGifWithoutKeyword() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(twitterBotController).build();

        mockMvc.perform(get("/trigger-search-and-reply"))
                .andExpect(status().isBadRequest());
    }
}