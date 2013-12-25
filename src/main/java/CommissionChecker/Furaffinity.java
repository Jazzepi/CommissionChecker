package CommissionChecker;

import CommissionChecker.logger.Logger;
import org.apache.commons.logging.Log;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class Furaffinity extends CommissionWebsite {

    @Autowired
    private JSoupWrapper jsoup;
    @Autowired
    @Qualifier("furaffinityUsername")
    private String username;
    @Autowired
    @Qualifier("furaffinityPassword")
    private String password;
    @Resource(name = "furaffinityUserList")
    private List<String> watchedUsernames;
    private Map<String, String> cookies = new HashMap<String, String>();
    @Logger
    private Log log;

    @Override
    public boolean isLoggedIn() throws IOException {
        log.info("Checking to see if I'm logged in to Furaffinity.");
        return !jsoup.connect("https://www.furaffinity.net/login/?ref=http://www.furaffinity.net/")
                .cookies(cookies)
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .method(Connection.Method.POST)
                .execute().parse().select("#my-username").isEmpty();
    }

    @Override
    public void login() throws IOException {
        cookies = jsoup.connect("https://www.furaffinity.net/login/?ref=http://www.furaffinity.net/")
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .data("retard_protection", "1")
                .data("name", username)
                .data("pass", password)
                .data("login", "Login to FurAffinity")
                .data("action", "login")
                .method(Connection.Method.POST)
                .execute()
                .cookies();
    }

    @Override
    public List<JournalEntry> fetchJournalEntries() throws IOException {
        Elements elements = jsoup.connect("http://www.furaffinity.net/msg/others/")
                .cookies(cookies)
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .get()
                .select("#messages-journals .message-stream li:not(.section-controls)");
        ArrayList<JournalEntry> journalEntries = new ArrayList<JournalEntry>(elements.size());
        for (Element element : elements) {
            journalEntries.add(new JournalEntry(element.childNode(4).childNode(0).toString(), element.childNode(2).childNode(0).toString()));
        }
        return journalEntries;
    }

    @Override
    public boolean isWatchedUser(String username) {
        return watchedUsernames.contains(username.toLowerCase());
    }

}
