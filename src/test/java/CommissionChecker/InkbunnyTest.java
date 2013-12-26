package CommissionChecker;

import org.apache.commons.logging.Log;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InkbunnyTest implements CommissionWebsiteTest{

    @Mock
    Log logMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    JSoupWrapper jSoupWrapperMock;


    @Override
    @Test
    public void is_logged_in_succeeds_if_logged_in_details_are_present() throws IOException, AWTException {
        Inkbunny inkbunny = new Inkbunny(jSoupWrapperMock, "AUserName", "password");
        ReflectionTestUtils.setField(inkbunny, "log", logMock);
        Document documentFake = Document.createShell("fakeUri");
        documentFake.body().append("<table class=\"loggedin_userdetails\"></div>");
        when(jSoupWrapperMock.connect(anyString())
                .cookies(anyMapOf(String.class, String.class))
                .timeout(anyInt())
                .method(any(Connection.Method.class))
                .execute().parse()).thenReturn(documentFake);

        boolean answer = inkbunny.isLoggedIn();

        assertTrue(answer);
    }

    @Override
    @Test
    public void is_logged_in_fails_if_logged_in_details_are_not_present() throws IOException, AWTException {
        Inkbunny inkbunny = new Inkbunny(jSoupWrapperMock, "AUserName", "password");
        ReflectionTestUtils.setField(inkbunny, "log", logMock);
        Document documentFake = Document.createShell("fakeUri");
        documentFake.body().append("<table class=\"loggedin_userdetails_missing\"></div>");
        when(jSoupWrapperMock.connect(anyString())
                .cookies(anyMapOf(String.class, String.class))
                .timeout(anyInt())
                .method(any(Connection.Method.class))
                .execute().parse()).thenReturn(documentFake);

        boolean answer = inkbunny.isLoggedIn();

        assertFalse(answer);
    }

    @Override
    @Test
    public void returns_all_journals() throws IOException, AWTException {
        //TODO implement this
        //        Inkbunny inkbunny = new Inkbunny(jSoupWrapperMock, "AUserName", "password");
//        ReflectionTestUtils.setField(inkbunny, "log", logMock);
//        Document documentFake = Document.createShell("fakeUri");
//        documentFake.body().append("<table class=\"loggedin_userdetails_missing\"></div>");
//        when(jSoupWrapperMock.connect("https://inkbunny.net/index.php")
//                .cookies(anyMapOf(String.class, String.class))
//                .timeout(anyInt())
//                .method(any(Connection.Method.class))
//                .execute().parse()).thenReturn(documentFake);
//
//        JournalEntry journalEntry = new JournalEntry("user1", "keyword1");
//        JournalEntry journalEntry = new JournalEntry("user2", "keyword2");
//        new ArrayList<JournalEntry>(Arrays.asList(journalEntry));
//
//        boolean answer = inkbunny.fetchJournalEntries();
//
//        assertFalse(answer);
    }
}