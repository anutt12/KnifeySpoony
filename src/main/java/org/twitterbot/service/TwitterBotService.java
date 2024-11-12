package org.twitterbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.*;

import java.io.File;

/**
 * TwitterBotService is responsible for searching tweets containing a
 * specific keyword and replying to them with a GIF.
 * <p>
 * This class interacts with the Twitter API to perform search and reply operations.
 */
@Service
public class TwitterBotService {

    @Value("${gif.path}")  // Inject the GIF path from application.properties
    private String gifPath;

    private final Twitter twitter;

    public TwitterBotService(Twitter twitter) {
        this.twitter = twitter;
    }

    public void searchAndReplyWithGif(String keyword) {
        final String DEFAULT_KEYWORD = "knife";  // Default to "knife"
        final int TWEET_COUNT = 10;

        // If the keyword is empty or null, default to "knife"
        if (keyword == null || keyword.isEmpty()) {
            keyword = DEFAULT_KEYWORD;
        }

        try {
            Query query = createQuery(keyword, TWEET_COUNT);
            QueryResult result = twitter.search(query);

            for (Status status : result.getTweets()) {
                if (shouldReplyToStatus(status)) {
                    String mediaId = uploadGif(gifPath);  // Use the path from properties

                    if (mediaId != null) {
                        replyWithGif(status, mediaId);
                        System.out.println("Replied to @" + status.getUser().getScreenName() + " with a GIF");
                    }
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    Query createQuery(String keyword, int count) {
        Query query = new Query(keyword);
        query.setCount(count);
        return query;
    }

    boolean shouldReplyToStatus(Status status) {
        return !status.isRetweet() && !status.getUser().isProtected();
    }

    void replyWithGif(Status status, String mediaId) throws TwitterException {
        String replyText = "@" + status.getUser().getScreenName();
        StatusUpdate statusUpdate = new StatusUpdate(replyText);
        statusUpdate.inReplyToStatusId(status.getId());
        statusUpdate.setMediaIds(Long.parseLong(mediaId));
        twitter.updateStatus(statusUpdate);
    }

    String uploadGif(String filePath) {
        try {
            File gifFile = new File(filePath);
            long mediaId = twitter.uploadMedia(gifFile).getMediaId();
            return String.valueOf(mediaId);
        } catch (TwitterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
