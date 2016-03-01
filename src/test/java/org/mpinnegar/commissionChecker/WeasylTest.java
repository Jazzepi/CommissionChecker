package org.mpinnegar.commissionChecker;

import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WeasylTest implements CommissionWebsiteTest {

    @Mock
    Logger logMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    JSoupWrapper jSoupWrapperMock;

    @Test
    @Override
    public void is_logged_in_succeeds_if_logged_in_details_are_present() throws IOException, AWTException {
        Weasyl weasyl = new Weasyl(jSoupWrapperMock, "apikey");
        Document documentFake = Document.createShell("fakeUri");
        documentFake.body().append("<div id=\"header-user\"></div>");
        when(jSoupWrapperMock.connect(anyString())
                .timeout(anyInt())
                .header(anyString(), anyString())
                .get()).thenReturn(documentFake);

        assertThat(weasyl.isLoggedIn()).isTrue();
    }

    @Test
    @Override
    public void is_logged_in_fails_if_logged_in_details_are_not_present() throws IOException, AWTException {
        Weasyl weasyl = new Weasyl(jSoupWrapperMock, "apikey");
        Document documentFake = Document.createShell("fakeUri");
        documentFake.body().append("<div id=\"header-user-missing\"></div>");
        when(jSoupWrapperMock.connect(anyString())
                .timeout(anyInt())
                .header(anyString(), anyString())
                .get()).thenReturn(documentFake);

        assertThat(weasyl.isLoggedIn()).isFalse();
    }

    @Test
    @Override
    public void returns_all_journals() throws IOException, AWTException {
        Weasyl weasyl = new Weasyl(jSoupWrapperMock, "apikey");
        Document documentFake = Document.createShell("fakeUri");
        documentFake.body().append("<div id=\"messages-checkboxes\">" +
                "<h3>Journals</h3>" +
                "<div class=\"group\">" +
                "<div class=\"item\">" +
                "<input type=\"checkbox\" name=\"36219_0_21427_1010\" id=\"36219_0_21427_1010\">" +
                "<a href=\"/profile/User1\" class=\"username\">User1</a> posted a journal entry titled <a href=\"/journal/21427/christmas-roulette-tonight\">Christmas roulette tonight</a>." +
                "<span class=\"date color-lighter\">23 December 2013</span>" +
                "</div>" +
                "<div class=\"item\">" +
                "<input type=\"checkbox\" name=\"36219_0_21427_1010\" id=\"36219_0_21427_1010\" >" +
                "<a href=\"/profile/User2\" class=\"username\">User2</a> posted a journal entry titled <a href=\"/journal/21427/christmas-roulette-tonight\">Christmas roulette tonight2</a>." +
                "</div>" +
                "</div>" +
                "</div>");
        when(jSoupWrapperMock.connect(anyString())
                .timeout(anyInt())
                .header(anyString(), anyString())
                .get()).thenReturn(documentFake);
        //TODO when I get a journal entry on Weasyl
        JournalEntry journalEntry1 = new JournalEntry("User1", "Christmas roulette tonight", null);
        JournalEntry journalEntry2 = new JournalEntry("User2", "Christmas roulette tonight2", null);

        assertThat(weasyl.fetchJournalEntries()).contains(journalEntry1, journalEntry2);
    }
}
