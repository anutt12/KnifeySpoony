package org.twitterbot.service;

import org.springframework.stereotype.Service;
import twitter4j.*;

import java.io.File;

@Service
public class TwitterBotService {


    private final Twitter twitter;

    public TwitterBotService(Twitter twitter) {
        this.twitter = twitter;
    }

    public void searchAndReply(String keyword) {
        keyword = "knife";
        try {
            Query query = new Query(keyword);
            query.setCount(10);  // Limit the number of tweets returned

            QueryResult result = twitter.search(query);

            for (Status status : result.getTweets()) {
                String user = status.getUser().getScreenName();
                String replyText = "@" + user;

                // Avoid replying to retweets and protected tweets
                if (!status.isRetweet() && !status.getUser().isProtected()) {
                    // Upload the GIF and get the media ID
                    String mediaId = uploadGif("src/main/resources/giphy.webp");

                    if (mediaId != null) {
                        // Create a status update with the reply text and the media ID
                        StatusUpdate statusUpdate = new StatusUpdate(replyText);
                        statusUpdate.inReplyToStatusId(status.getId());
                        statusUpdate.setMediaIds(Long.parseLong(mediaId)); // Attach the media (GIF)

                        twitter.updateStatus(statusUpdate);
                        System.out.println("Replied to @" + user + " with a GIF");
                    }
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    // Method to upload the GIF to Twitter and get the media ID
    private String uploadGif(String filePath) {
        try {
            // Upload the GIF file
            File gifFile = new File(filePath);
            long mediaId = twitter.uploadMedia(gifFile).getMediaId();
            return String.valueOf(mediaId);
        } catch (TwitterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
