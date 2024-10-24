package org.twitterbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import twitter4j.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitterBotServiceTest {

    @InjectMocks
    private TwitterBotService twitterBotService; // Class under test

    @Mock
    private Twitter twitter; // Mocked Twitter dependency

    private static final String GIF_PATH = "/path/to/mock/gif.gif";
    private static final String MOCK_USER_SCREEN_NAME = "mockUser";
    private static final long MOCK_MEDIA_ID = 123456L;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(twitterBotService, "gifPath", GIF_PATH);
    }

    @Test
    void testSearchAndReplyWithGif_Success() throws TwitterException {
        // Mock dependencies and their behavior
        setupMockTwitterBehaviorForSearchAndReplyWithGif();

        // Mock the response of the updateStatus method to return a valid Status object
        Status mockStatus = mock(Status.class);
        when(twitter.updateStatus(any(StatusUpdate.class))).thenReturn(mockStatus);

        // Call the method under test
        twitterBotService.searchAndReplyWithGif("knife");

        // Verify interactions with Twitter
        verify(twitter, times(1)).search(any(Query.class));
        verify(twitter, times(1)).uploadMedia(any(File.class));

        // Capture the StatusUpdate object passed to updateStatus
        ArgumentCaptor<StatusUpdate> statusUpdateCaptor = ArgumentCaptor.forClass(StatusUpdate.class);
        verify(twitter, times(1)).updateStatus(statusUpdateCaptor.capture());

        StatusUpdate capturedStatusUpdate = statusUpdateCaptor.getValue();

        // Assert the captured StatusUpdate
        assertNotNull(capturedStatusUpdate);
        assertTrue(capturedStatusUpdate.getStatus().contains("knife"));

        // Verifying that media ID is set correctly using reflection
        long[] mediaIds = (long[]) ReflectionTestUtils.getField(capturedStatusUpdate, "mediaIds");
        assertNotNull(mediaIds);
        assertEquals(1, mediaIds.length);
        assertEquals(MOCK_MEDIA_ID, mediaIds[0]);
    }

    private void setupMockTwitterBehaviorForSearchAndReplyWithGif() throws TwitterException {
        // Mock QueryResult and its behavior
        QueryResult mockQueryResult = mock(QueryResult.class);
        Status mockStatus = mock(Status.class);
        User mockUser = mock(User.class);

        // Mock user and status behaviors
        when(mockUser.getScreenName()).thenReturn(MOCK_USER_SCREEN_NAME);
        when(mockStatus.getUser()).thenReturn(mockUser);
        when(mockStatus.isRetweet()).thenReturn(false);
        when(mockUser.isProtected()).thenReturn(false);

        // Mock the list of statuses returned by the query
        List<Status> tweets = Collections.singletonList(mockStatus);
        when(mockQueryResult.getTweets()).thenReturn(tweets);
        when(twitter.search(any(Query.class))).thenReturn(mockQueryResult);

        // Mock uploadMedia behavior
        UploadedMedia mockUploadedMedia = mock(UploadedMedia.class);
        when(mockUploadedMedia.getMediaId()).thenReturn(MOCK_MEDIA_ID);
        when(twitter.uploadMedia(any(File.class))).thenReturn(mockUploadedMedia);
    }

    @Test
    void testSearchAndReplyWithGif_NoMatchingTweets() throws TwitterException {
        QueryResult mockResult = mock(QueryResult.class);
        when(mockResult.getTweets()).thenReturn(new ArrayList<>());
        when(twitter.search(any(Query.class))).thenReturn(mockResult);

        twitterBotService.searchAndReplyWithGif("knife");

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

        assertTrue(twitterBotService.shouldReplyToStatus(mockStatus));

        when(mockStatus.isRetweet()).thenReturn(true);
        assertFalse(twitterBotService.shouldReplyToStatus(mockStatus));

        when(mockUser.isProtected()).thenReturn(true);
        when(mockStatus.isRetweet()).thenReturn(false);
        assertFalse(twitterBotService.shouldReplyToStatus(mockStatus));
    }

    @Test
    void testCreateQuery() {
        String keyword = "knife";
        Query query = twitterBotService.createQuery(keyword, 10);

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
        when(mockUser.getScreenName()).thenReturn(MOCK_USER_SCREEN_NAME);

        twitterBotService.replyWithGif(mockStatus, String.valueOf(MOCK_MEDIA_ID));

        ArgumentCaptor<StatusUpdate> captor = ArgumentCaptor.forClass(StatusUpdate.class);
        verify(twitter).updateStatus(captor.capture());

        StatusUpdate capturedStatusUpdate = captor.getValue();
        assertNotNull(capturedStatusUpdate);
        assertEquals("@" + MOCK_USER_SCREEN_NAME, capturedStatusUpdate.getStatus());
        assertEquals(12345L, capturedStatusUpdate.getInReplyToStatusId());
    }

    @Test
    void testUploadGif_Success() throws TwitterException {
        UploadedMedia mockUploadedMedia = mock(UploadedMedia.class);
        when(mockUploadedMedia.getMediaId()).thenReturn(MOCK_MEDIA_ID);
        when(twitter.uploadMedia(any(File.class))).thenReturn(mockUploadedMedia);

        String mediaId = twitterBotService.uploadGif(GIF_PATH);

        assertNotNull(mediaId);
        assertEquals(String.valueOf(MOCK_MEDIA_ID), mediaId);
    }

    @Test
    void testUploadGif_Failure() throws TwitterException {
        when(twitter.uploadMedia(any(File.class))).thenThrow(new TwitterException("Failed to upload"));

        String mediaId = twitterBotService.uploadGif(GIF_PATH);

        assertNull(mediaId);
    }
}