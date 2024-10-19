package org.twitterbot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.twitterbot.service.TwitterBotService;

@RestController
public class TwitterBotController {

    private final TwitterBotService twitterBotService;

    public TwitterBotController(TwitterBotService twitterBotService) {
        this.twitterBotService = twitterBotService;
    }

    // Define a GET endpoint to trigger the searchAndReply method
    @GetMapping("/trigger-bot")
    public String triggerBot(@RequestParam String keyword) {
        twitterBotService.searchAndReply(keyword);
        return "Bot triggered for keyword: " + keyword;
    }
}
