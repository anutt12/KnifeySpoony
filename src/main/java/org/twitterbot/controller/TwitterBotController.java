package org.twitterbot.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.twitterbot.service.TwitterBotService;


/**
 * TwitterBotController is a REST controller that provides endpoints to interact
 * with a Twitter bot. This bot searches for tweets containing a specified keyword
 * and replies to them with a GIF.
 * <p>
 * This controller also implements CommandLineRunner to allow triggering the bot
 * via command-line arguments.
 */
@RestController
public class TwitterBotController implements CommandLineRunner {
    private final TwitterBotService twitterBotService;

    private static final String TRIGGERED_MESSAGE = "Bot triggered for keyword: ";
    private static final String NO_KEYWORD_MESSAGE = "No keyword provided";

    public TwitterBotController(TwitterBotService twitterBotService) {
        this.twitterBotService = twitterBotService;
    }

    @GetMapping("/trigger-search-and-reply")  // Use @GetMapping to explicitly handle GET requests
    public String triggerSearchAndReplyWithGif(@RequestParam String keyword) {
        twitterBotService.searchAndReplyWithGif(keyword);
        return TRIGGERED_MESSAGE + keyword;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            triggerBotWithKeyword(args[0]);
        } else {
            System.out.println(NO_KEYWORD_MESSAGE);
        }
    }

    private void triggerBotWithKeyword(String keyword) {
        twitterBotService.searchAndReplyWithGif(keyword);
    }
}
