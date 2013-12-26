package CommissionChecker;

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
public class Weasyl extends CommissionWebsite{

    private final JSoupWrapper jsoup;
    private final String username;
    private final String password;
    private Map<String, String> cookies = new HashMap<String, String>();
    @Resource(name = "weasylUserList")
    private List watchedUsernames;

    @Autowired
    Weasyl(JSoupWrapper jsoup, @Qualifier("weasylUsername") String username, @Qualifier("weasylPassword") String password) {
        this.jsoup = jsoup;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean isLoggedIn() throws IOException {
        return !jsoup.connect("https://www.weasyl.com/")
                .cookies(cookies)
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .get()
                .select("#header-user")
                .isEmpty();
    }

    @Override
    public void login() throws IOException {
        cookies = jsoup.connect("https://www.weasyl.com/signin")
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .data("username", username)
                .data("password", password)
                .header("Host", "www.weasyl.com")
                .header("Origin", "https://www.weasyl.com")
                .header("Referer", "https://www.weasyl.com/signin")
                .method(Connection.Method.POST)
                .execute().cookies();
    }

    @Override
    public List<JournalEntry> fetchJournalEntries() throws IOException {
        Elements elements = jsoup.connect("https://www.weasyl.com/messages/notifications")
                .cookies(cookies)
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .get()
                .select("h3:containsOwn(Journals) + div.group div.item");

        ArrayList<JournalEntry> journalEntries = new ArrayList<JournalEntry>(elements.size());
        for (Element element : elements) {
            journalEntries.add(new JournalEntry(element.select("a.username").text(), element.select("a.username ~ a").text()));
        }
        return journalEntries;

    }

    @Override
    public boolean isWatchedUser(String username) {
        return watchedUsernames.contains(username.toLowerCase());
    }
}
