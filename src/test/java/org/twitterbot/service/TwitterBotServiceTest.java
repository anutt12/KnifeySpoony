package org.twitterbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import twitter4j.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitterBotServiceTest {

    @InjectMocks
    private TwitterBotService twitterBotService;  // Class under test

    @Mock
    private Twitter twitter;  // Mocked Twitter dependency

    private final String gifPath = "/path/to/mock/gif.gif";

    @BeforeEach
    public void setUp() {
        // Inject the mock GIF path into the service
        ReflectionTestUtils.setField(twitterBotService, "gifPath", gifPath);
    }

    @Test
    void testSearchAndReplyWithGif_Success() throws TwitterException {
        // Mock a Twitter QueryResult and Status objects
        QueryResult mockResult = mock(QueryResult.class);
        Status mockStatus = mock(Status.class);
        User mockUser = mock(User.class);

        // Set up mock status and user behavior
        when(mockUser.getScreenName()).thenReturn("mockUser");
        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockStatus.isRetweet()).thenReturn(false);
        when(mockUser.isProtected()).thenReturn(false);

        // Mock the search result
        List<Status> tweets = new ArrayList<>();
        tweets.add(mockStatus);
        when(mockResult.getTweets()).thenReturn(tweets);
        when(twitter.search(any(Query.class))).thenReturn(mockResult);

        // Mock the upload GIF behavior
        UploadedMedia mockUploadedMedia = mock(UploadedMedia.class);  // Mock UploadedMedia
        when(mockUploadedMedia.getMediaId()).thenReturn(123456L);     // Simulate the media ID
        when(twitter.uploadMedia(any(File.class))).thenReturn(mockUploadedMedia); // Return the mock

        // Mock the update status behavior
        doNothing().when(twitter).updateStatus(any(StatusUpdate.class));

        // Call the method under test
        twitterBotService.searchAndReplyWithGif("knife");

        // Verify that the methods were called correctly
        verify(twitter, times(1)).search(any(Query.class));
        verify(twitter, times(1)).uploadMedia(any(File.class));
        verify(twitter, times(1)).updateStatus(any(StatusUpdate.class));
    }

    @Test
    void testSearchAndReplyWithGif_NoMatchingTweets() throws TwitterException {
        // Mock an empty result set
        QueryResult mockResult = mock(QueryResult.class);
        when(mockResult.getTweets()).thenReturn(new ArrayList<>());
        when(twitter.search(any(Query.class))).thenReturn(mockResult);

        // Call the method under test
        twitterBotService.searchAndReplyWithGif("knife");

        // Verify that no reply or upload is performed
        verify(twitter, times(1)).search(any(Query.class));
        verify(twitter, never()).uploadMedia(any(File.class));
        verify(twitter, never()).updateStatus(any(StatusUpdate.class));
    }

    @Test
    void testShouldReplyToStatus() {
        Status mockStatus = mock(Status.class);
        User mockUser = mock(User.class);

        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockStatus.isRetweet()).thenReturn(false);
        when(mockUser.isProtected()).thenReturn(false);

        // Test the method under different scenarios
        assertTrue(twitterBotService.shouldReplyToStatus(mockStatus));

        when(mockStatus.isRetweet()).thenReturn(true);
        assertFalse(twitterBotService.shouldReplyToStatus(mockStatus));

        when(mockStatus.isRetweet()).thenReturn(false);
        when(mockUser.isProtected()).thenReturn(true);
        assertFalse(twitterBotService.shouldReplyToStatus(mockStatus));
    }

    @Test
    void testCreateQuery() {
        String keyword = "knife";
        Query query = twitterBotService.createQuery(keyword, 10);

        // Verify the query object properties
        assertNotNull(query);
        assertEquals("knife", query.getQuery());
        assertEquals(10, query.getCount());
    }

    @Test
    void testReplyWithGif() throws TwitterException {
        Status mockStatus = mock(Status.class);
        User mockUser = mock(User.class);

        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockStatus.getId()).thenReturn(12345L);
        when(mockUser.getScreenName()).thenReturn("mockUser");

        // Call the method under test
        twitterBotService.replyWithGif(mockStatus, "123456");

        // Verify that the updateStatus method was called with the correct parameters
        verify(twitter, times(1)).updateStatus(any(StatusUpdate.class));
    }

    @Test
    void testUploadGif_Success() throws TwitterException {
        // Mock the UploadedMedia behavior
        UploadedMedia mockUploadedMedia = Mockito.mock(UploadedMedia.class);
        when(mockUploadedMedia.getMediaId()).thenReturn(123456L);  // Simulate the media ID

        // Mock the Twitter uploadMedia method to return the mock UploadedMedia
        when(twitter.uploadMedia(any(File.class))).thenReturn(mockUploadedMedia);

        // Call the method under test
        String mediaId = twitterBotService.uploadGif(gifPath);

        // Verify the result
        assertNotNull(mediaId);
        assertEquals("123456", mediaId);
    }

    @Test
    void testUploadGif_Failure() throws TwitterException {
        // Simulate a failure in uploading
        when(twitter.uploadMedia(any(File.class))).thenThrow(new TwitterException("Failed to upload"));

        // Call the method under test
        String mediaId = twitterBotService.uploadGif(gifPath);

        // Verify the result
        assertNull(mediaId);
    }
}