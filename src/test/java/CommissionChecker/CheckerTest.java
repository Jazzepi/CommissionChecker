package CommissionChecker;

import org.apache.commons.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckerTest {

    @Mock
    Furaffinity furaffinityMock;
    @Mock
    Log logMock;

    @Test
    public void checker_logs_in_when_site_is_not_logged_in() throws IOException {
        Checker checker = new Checker();
        ReflectionTestUtils.setField(checker, "log", logMock);
        when(furaffinityMock.isLoggedIn()).thenReturn(false);

        List<JournalEntry> result = checker.check(furaffinityMock);

        verify(furaffinityMock).login();
    }

    @Test
    public void checker_does_not_log_in_when_site_is_already_logged_in() throws IOException {
        Checker checker = new Checker();
        ReflectionTestUtils.setField(checker, "log", logMock);
        when(furaffinityMock.isLoggedIn()).thenReturn(true);

        List<JournalEntry> result = checker.check(furaffinityMock);

        verify(furaffinityMock, never()).login();
    }

    @Test
    public void checker_filters_journals_without_watched_users() throws IOException {
        Checker checker = new Checker();
        ReflectionTestUtils.setField(checker, "log", logMock);
        ReflectionTestUtils.setField(checker, "commissionKeywords", new ArrayList<String>(Arrays.asList("keyword1","keyword2", "keyword3")));
        JournalEntry journalEntry = new JournalEntry("user2", "keyword2");
        when(furaffinityMock.fetchJournalEntries()).thenReturn(new ArrayList<JournalEntry>(Arrays.asList(journalEntry)));
        when(furaffinityMock.isWatchedUser("user2")).thenReturn(false);

        List<JournalEntry> result = checker.check(furaffinityMock);

        assertTrue(result.isEmpty());
    }

    @Test
    public void checker_filters_journals_without_watched_keywords() throws IOException {
        Checker checker = new Checker();
        ReflectionTestUtils.setField(checker, "log", logMock);
        ReflectionTestUtils.setField(checker, "commissionKeywords", new ArrayList<String>(Arrays.asList("keyword1","keyword2", "keyword3")));
        JournalEntry journalEntry = new JournalEntry("user2", "key");
        when(furaffinityMock.fetchJournalEntries()).thenReturn(new ArrayList<JournalEntry>(Arrays.asList(journalEntry)));
        when(furaffinityMock.isWatchedUser("user2")).thenReturn(true);

        List<JournalEntry> result = checker.check(furaffinityMock);

        assertTrue(result.isEmpty());
    }

    @Test
    public void checker_filters_journals_that_have_already_been_reported() throws IOException {
        Checker checker = new Checker();
        ReflectionTestUtils.setField(checker, "log", logMock);
        ReflectionTestUtils.setField(checker, "commissionKeywords", new ArrayList<String>(Arrays.asList("keyword1","keyword2","keyword3")));
        JournalEntry journalEntry1 = new JournalEntry("user2", "keyword1 is ready!");
        JournalEntry journalEntry2 = new JournalEntry("user2", "keyword1 is ready!");
        when(furaffinityMock.fetchJournalEntries()).thenReturn(new ArrayList<JournalEntry>(Arrays.asList(journalEntry1, journalEntry2)));
        when(furaffinityMock.isWatchedUser("user2")).thenReturn(true);

        List<JournalEntry> result = checker.check(furaffinityMock);

        assertEquals(result, new ArrayList<JournalEntry>(Arrays.asList(new JournalEntry("user2", "keyword1 is ready!"))));
    }

}
