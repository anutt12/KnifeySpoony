package org.twitterbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
public class TwitterConfig {

    private static final String CONSUMER_KEY_PROPERTY = "twitter4j.oauth.consumerKey";
    private static final String CONSUMER_SECRET_PROPERTY = "twitter4j.oauth.consumerSecret";
    private static final String ACCESS_TOKEN_PROPERTY = "twitter4j.oauth.accessToken";
    private static final String ACCESS_TOKEN_SECRET_PROPERTY = "twitter4j.oauth.accessTokenSecret";

    @Value("${" + CONSUMER_KEY_PROPERTY + "}")
    private String consumerKey;

    @Value("${" + CONSUMER_SECRET_PROPERTY + "}")
    private String consumerSecret;

    @Value("${" + ACCESS_TOKEN_PROPERTY + "}")
    private String accessToken;

    @Value("${" + ACCESS_TOKEN_SECRET_PROPERTY + "}")
    private String accessTokenSecret;

    @Bean
    public Twitter twitter() {
        log.info("Creating Twitter instance with provided configuration properties.");
        ConfigurationBuilder configurationBuilder = buildTwitterConfiguration();
        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
        return twitterFactory.getInstance();
    }

    private ConfigurationBuilder buildTwitterConfiguration() {
        return new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
    }
}