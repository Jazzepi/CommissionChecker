package org.mpinnegar.commissionChecker;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FuraffinityTest implements CommissionWebsiteTest {

    @Mock
    Logger logMock;
    @Mock
    Runner runnerMock;
    @Mock
    JBrowserDriverManager jBrowserDriverManagerMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    JBrowserDriver jBrowserDriverMock;


    @Before
    public void setup() {
        when(jBrowserDriverManagerMock.getDriver()).thenReturn(jBrowserDriverMock);
        when(jBrowserDriverMock.getStatusCode()).thenReturn(200);
    }

    @Override
    @Test
    public void is_logged_in_succeeds_if_logged_in_details_are_present() throws IOException, AWTException {
        Furaffinity furaffinity = new Furaffinity(runnerMock, "AUserName", "password", jBrowserDriverManagerMock);
        ReflectionTestUtils.setField(furaffinity, "log", logMock);
        when(jBrowserDriverMock.findElementsById("my-username")).thenReturn(Arrays.asList(mock(WebElement.class)));

        assertThat(furaffinity.isLoggedIn()).isTrue();
    }

    @Override
    @Test
    public void is_logged_in_fails_if_logged_in_details_are_not_present() throws IOException, AWTException {
        Furaffinity furaffinity = new Furaffinity(runnerMock, "AUserName", "password", jBrowserDriverManagerMock);
        ReflectionTestUtils.setField(furaffinity, "log", logMock);
        when(jBrowserDriverMock.findElementsById("my-username")).thenReturn(Arrays.asList());

        assertThat(furaffinity.isLoggedIn()).isFalse();
    }

    @Override
    @Test
    public void returns_all_journals() throws IOException, AWTException {
        Furaffinity furaffinity = new Furaffinity(runnerMock, "AUserName", "password", jBrowserDriverManagerMock);
        ReflectionTestUtils.setField(furaffinity, "log", logMock);

        WebElement usernameWebElementMock = mock(WebElement.class);
        when(usernameWebElementMock.getText()).thenReturn("xaxoqual");
        WebElement journalTitleWebElementMock = mock(WebElement.class);
        when(journalTitleWebElementMock.getText()).thenReturn("Merry Christmas and Stuff");
        when(journalTitleWebElementMock.getAttribute("href")).thenReturn("http://www.furaffinity.net/journal/numbers/");
        WebElement webElementMock = mock(WebElement.class);
        when(webElementMock.findElement(By.cssSelector("a:nth-of-type(2)"))).thenReturn(usernameWebElementMock);
        when(webElementMock.findElement(By.cssSelector("a:nth-of-type(1)"))).thenReturn(journalTitleWebElementMock);

        when(jBrowserDriverMock.findElementsByCssSelector("#messages-journals .message-stream li:not(.section-controls)")).thenReturn(Arrays.asList(webElementMock));

        JournalEntry journalEntry1 = new JournalEntry("xaxoqual", "Merry Christmas and Stuff", "http://www.furaffinity.net/journal/numbers/");

        assertThat(furaffinity.fetchJournalEntries()).contains(journalEntry1);
    }
}