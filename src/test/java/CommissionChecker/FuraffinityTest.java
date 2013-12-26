package CommissionChecker;

import org.apache.commons.logging.Log;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FuraffinityTest implements CommissionWebsiteTest {

    @Mock
    Log logMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    JSoupWrapper jSoupWrapperMock;


    @Override
    @Test
    public void is_logged_in_succeeds_if_logged_in_details_are_present() throws IOException, AWTException {
        Furaffinity furaffinity = new Furaffinity(jSoupWrapperMock, "AUserName", "password");
        ReflectionTestUtils.setField(furaffinity, "log", logMock);
        Document documentFake = Document.createShell("fakeUri");
        documentFake.body().append("<li><a id=\"my-username\" href=\"/user/userName/\">~userName</a></li>");
        when(jSoupWrapperMock.connect(anyString())
                .cookies(anyMapOf(String.class, String.class))
                .timeout(anyInt())
                .get()).thenReturn(documentFake);

        boolean answer = furaffinity.isLoggedIn();

        assertTrue(answer);
    }

    @Override
    @Test
    public void is_logged_in_fails_if_logged_in_details_are_not_present() throws IOException, AWTException {
        Furaffinity furaffinity = new Furaffinity(jSoupWrapperMock, "AUserName", "password");
        ReflectionTestUtils.setField(furaffinity, "log", logMock);
        Document documentFake = Document.createShell("fakeUri");
        documentFake.body().append("<li><a id=\"my-username-id-is-missing\" href=\"/user/userName/\">~userName</a></li>");
        when(jSoupWrapperMock.connect(anyString())
                .cookies(anyMapOf(String.class, String.class))
                .timeout(anyInt())
                .get()).thenReturn(documentFake);

        boolean answer = furaffinity.isLoggedIn();

        assertFalse(answer);
    }

    @Override
    @Test
    public void returns_all_journals() throws IOException, AWTException {
        Furaffinity furaffinity = new Furaffinity(jSoupWrapperMock, "AUserName", "password");
        ReflectionTestUtils.setField(furaffinity, "log", logMock);
        Document documentFake = Document.createShell("fakeUri");
        documentFake.body().append("<fieldset id=\"messages-journals\">" +
                "<h3><input type=\"submit\" class=\"button\" name=\"nuke-journals\" value=\"Nuke journals\">New journals</h3>" +
                "<ul class=\"message-stream\">" +
                "<li class=\"\"><input type=\"checkbox\" name=\"journals[]\" value=\"5354177\">\"<a href=\"/journal/5354177/\">Merry Christmas and Stuff</a>\", posted by <a href=\"/user/xaxoqual/\">xaxoqual</a> <span title=\"on December 25th, 2013 01:37 AM\" class=\"popup_date\">a day ago</span></li>" +
                "<li class=\"\"><input type=\"checkbox\" name=\"journals[]\" value=\"5353885\">\"<a href=\"/journal/5353885/\">Message from the Artist:</a>\", posted by <a href=\"/user/syrae-universe/\">Syrae-Universe</a> <span title=\"on December 25th, 2013 12:15 AM\" class=\"popup_date\">a day ago</span></li>" +
                "<li class=\"section-controls\">" +
                "<input class=\"button mark-all\" type=\"button\" value=\"Select all\">" +
                "<input class=\"button mark-none\" type=\"button\" value=\"Select none\">" +
                "<input class=\"button remove\" type=\"submit\" value=\"Remove selected\" name=\"remove-journals\">" +
                "</li>" +
                "</ul>" +
                "</fieldset>");
        when(jSoupWrapperMock.connect(anyString())
                .cookies(anyMapOf(String.class, String.class))
                .timeout(anyInt())
                .get()).thenReturn(documentFake);
        JournalEntry journalEntry1 = new JournalEntry("xaxoqual", "Merry Christmas and Stuff");
        JournalEntry journalEntry2 = new JournalEntry("Syrae-Universe", "Message from the Artist:");

        List<JournalEntry> answer = furaffinity.fetchJournalEntries();

        assertEquals(Arrays.asList(journalEntry1, journalEntry2), answer);
    }
}