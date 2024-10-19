package org.twitterbot.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.twitterbot.service.TwitterBotService;

@RestController
public class TwitterBotController implements CommandLineRunner {

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

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            // Trigger the bot with the first argument as the keyword
            twitterBotService.searchAndReply(args[0]);
        } else {
            System.out.println("No keyword provided");
        }
    }
}
