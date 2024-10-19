package org.twitterbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import lombok.extern.slf4j.Slf4j;


/**
 * TwitterConfig is a configuration class for setting up a Twitter instance using the Twitter4J library.
 * It uses Spring's @Configuration to allow context-based injection of Twitter configuration properties.
 *
 * Logging is enabled for this configuration through Lombok's @Slf4j annotation.
 * The Twitter instance is configured by reading OAuth properties from the application's configuration.
 *
 * Properties:
 * - twitter4j.oauth.consumerKey: Consumer Key for Twitter OAuth.
 * - twitter4j.oauth.consumerSecret: Consumer Secret for Twitter OAuth.
 * - twitter4j.oauth.accessToken: Access Token for Twitter OAuth.
 * - twitter4j.oauth.accessTokenSecret: Access Token Secret for Twitter OAuth.
 *
 * Methods:
 * - twitter(): Creates and returns a Twitter instance configured with the provided OAuth properties.
 */
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

    ConfigurationBuilder buildTwitterConfiguration() {
        return new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
    }
}