package org.twitterbot.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.twitterbot.knifeyspoony.KnifeySpoonyApplication;
import org.twitterbot.service.TwitterBotService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TwitterBotController.class)
@ContextConfiguration(classes = KnifeySpoonyApplication.class)
class TwitterBotControllerTest {

    @MockBean
    private TwitterBotService twitterBotService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void testTriggerSearchAndReplyWithKnife() throws Exception {
        String keyword = "knife";  // Ensure we test it with "knife"

        mockMvc.perform(get("/trigger-search-and-reply")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bot triggered for keyword: " + keyword)));

        verify(twitterBotService).searchAndReplyWithGif(keyword);
    }

    @Test
    @WithMockUser
    void testTriggerSearchAndReplyWithDefaultKeyword() throws Exception {
        // No keyword provided, should default to "knife"
        mockMvc.perform(get("/trigger-search-and-reply"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bot triggered for keyword: knife")));

        verify(twitterBotService).searchAndReplyWithGif("knife");
    }
}