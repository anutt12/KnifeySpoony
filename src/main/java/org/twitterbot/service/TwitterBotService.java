package org.twitterbot.service;

import org.springframework.stereotype.Service;
import twitter4j.*;

@Service
public class TwitterBotService {


    private final Twitter twitter;

    public TwitterBotService(Twitter twitter) {
        this.twitter = twitter;
    }

    public void searchAndReply(String keyword) {
        try {
            // Updated to use the new Query structure
            Query query = new Query(keyword);
            query.setCount(10);  // Limit number of tweets returned

            QueryResult result = twitter.search(query);

            for (Status status : result.getTweets()) {
                String user = status.getUser().getScreenName();
                String replyText = "@" + user + " Thanks for tweeting about " + keyword + "!";

                // Check for rate limits or existing replies
                if (!status.isRetweet() && !status.getUser().isProtected()) {
                    StatusUpdate statusUpdate = new StatusUpdate(replyText);
                    statusUpdate.inReplyToStatusId(status.getId());
                    twitter.updateStatus(statusUpdate);
                    System.out.println("Replied to @" + user);
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
