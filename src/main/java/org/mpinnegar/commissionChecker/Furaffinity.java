package org.mpinnegar.commissionChecker;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import org.mpinnegar.commissionChecker.logger.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class Furaffinity extends CommissionWebsite implements StateListener {
    private static final int MINUTES_TO_WAIT_FOR_CLOUDFLARE = 6;
    private String username;
    private String password;
    @Resource(name = "furaffinityUserList")
    private List<String> watchedUsernames;
    private Map<String, String> cookies = new HashMap<>();
    @Log
    private Logger log;
    private JBrowserDriverManager jBrowserDriverManager;
    private JBrowserDriver jBrowserDriver;

    @Autowired
    Furaffinity(Runner runner, @Qualifier("furaffinityUsername") String username, @Qualifier("furaffinityPassword") String password, JBrowserDriverManager jBrowserDriverManager) {
        this.jBrowserDriverManager = jBrowserDriverManager;
        jBrowserDriver = jBrowserDriverManager.getDriver();
        runner.registerStateListener(this);
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean isLoggedIn() throws IOException {
        jBrowserDriver.get("http://www.furaffinity.net/");
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(MINUTES_TO_WAIT_FOR_CLOUDFLARE));
        } catch (InterruptedException ignored) {
        }

        log.info(name() + " returned status code " + jBrowserDriver.getStatusCode() + " when checking to see if the user is logged in.");
        return jBrowserDriver.getStatusCode() == 200 && !jBrowserDriver.findElementsById("my-username").isEmpty();
    }

    @Override
    public boolean login() throws IOException {
        log.info("Logging in to " + name() + ".");
        jBrowserDriver.get("https://www.furaffinity.net/login/");
        jBrowserDriver.findElementByCssSelector("input[name=\"name\"]").sendKeys(username);
        jBrowserDriver.findElementByCssSelector("input[name=\"pass\"]").sendKeys(password);
        jBrowserDriver.findElementByCssSelector("input[name=\"login\"]").click();
        log.info(name() + " returned status code " + jBrowserDriver.getStatusCode() + " when logging in.");
        return jBrowserDriver.getStatusCode() == 200;
    }

    @Override
    public List<JournalEntry> fetchJournalEntries() throws IOException {
        log.info("Fetching journal enteries for " + name() + ".");
        jBrowserDriver.get("http://www.furaffinity.net/msg/others/#journals");
        return jBrowserDriver.findElementsByCssSelector("#messages-journals .message-stream li:not(.section-controls)").stream().
                map(
                        element -> {
                            WebElement journalLink = element.findElement(By.cssSelector("a:nth-of-type(1)"));
                            return new JournalEntry(
                                    element.findElement(By.cssSelector("a:nth-of-type(2)")).getText(),
                                    journalLink.getText(),
                                    journalLink.getAttribute("href")
                            );
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    public boolean isWatchedUser(String username) {
        return watchedUsernames.contains(username.toLowerCase());
    }

    @Override
    public void stateChange(ApplicationState newState) {
        switch (newState) {
            case RUNNING:
                jBrowserDriver = jBrowserDriverManager.getDriver();
                break;
            case SLEEPING:
                jBrowserDriver.quit();
                break;
        }
    }
}