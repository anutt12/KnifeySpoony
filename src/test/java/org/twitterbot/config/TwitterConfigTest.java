package org.twitterbot.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import twitter4j.Twitter;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitterConfigTest {

    @InjectMocks
    private TwitterConfig twitterConfig;  // The class we are testing

    @Mock
    private TwitterFactory twitterFactory;  // Mock the TwitterFactory

    @BeforeEach
    public void setUp() {
        // Mock values for OAuth properties
        ReflectionTestUtils.setField(twitterConfig, "consumerKey", "mockConsumerKey");
        ReflectionTestUtils.setField(twitterConfig, "consumerSecret", "mockConsumerSecret");
        ReflectionTestUtils.setField(twitterConfig, "accessToken", "mockAccessToken");
        ReflectionTestUtils.setField(twitterConfig, "accessTokenSecret", "mockAccessTokenSecret");
    }

    @Test
    void testTwitterBeanCreation() {
        // Mock the behavior of TwitterFactory
        Twitter mockTwitter = mock(Twitter.class);
        when(twitterFactory.getInstance()).thenReturn(mockTwitter);

        // Call the method under test
        Twitter twitterInstance = twitterConfig.twitter(twitterFactory);

        // Verify that the Twitter instance is created
        assertNotNull(twitterInstance);
        verify(twitterFactory, times(1)).getInstance();
    }

    @Test
    void testBuildTwitterConfiguration() {
        // Call the private buildTwitterConfiguration method using Reflection
        ConfigurationBuilder configBuilder = twitterConfig.buildTwitterConfiguration();

        // Verify that the properties are set correctly
        Configuration configuration = configBuilder.build();
        assertNotNull(configuration);
        assertNotNull(configuration.getOAuthConsumerKey());
        assertNotNull(configuration.getOAuthConsumerSecret());
        assertNotNull(configuration.getOAuthAccessToken());
        assertNotNull(configuration.getOAuthAccessTokenSecret());

        // Verify the actual values
        assert (configuration.getOAuthConsumerKey().equals("mockConsumerKey"));
        assert (configuration.getOAuthConsumerSecret().equals("mockConsumerSecret"));
        assert (configuration.getOAuthAccessToken().equals("mockAccessToken"));
        assert (configuration.getOAuthAccessTokenSecret().equals("mockAccessTokenSecret"));
    }
}