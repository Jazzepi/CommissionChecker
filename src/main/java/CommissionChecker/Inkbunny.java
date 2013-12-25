package CommissionChecker;

import CommissionChecker.logger.Logger;
import org.apache.commons.logging.Log;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
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
public class Inkbunny extends CommissionWebsite {


    private final JSoupWrapper jsoup;
    private final String username;
    private final String password;
    @Resource(name = "inkbunnyUserList")
    private List<String> watchedUsernames;
    private Map<String, String> cookies = new HashMap<String, String>();
    @Logger
    private Log log;

    @Autowired
    Inkbunny(JSoupWrapper jsoup, @Qualifier("inkbunnyUsername") String username, @Qualifier("inkbunnyPassword") String password) {
        this.jsoup = jsoup;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean isLoggedIn() throws IOException {
        log.info("Checking to see if I'm logged in to Inkbunny.");
        return !jsoup.connect("https://inkbunny.net/index.php")
                .cookies(cookies)
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .method(Connection.Method.GET)
                .execute()
                .parse()
                .select(".loggedin_userdetails")
                .isEmpty();
    }

    @Override
    public void login() throws IOException {
        log.info("Logging into Inkbunny with username " + username);
        Connection.Response response = jsoup.connect("https://inkbunny.net/login.php")
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .execute();

        cookies = response.cookies();

        String loginToken = response.parse().select("input[name=token]").attr("value");

        cookies = jsoup.connect("https://inkbunny.net/login_process.php")
                .cookies(cookies)
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .data("username", username)
                .data("password", password)
                .data("token", loginToken)
                .header("Origin", "https://inkbunny.net")
                .referrer("https://inkbunny.net/login.php")
                .header("Host", "inkbunny.net")
                .method(Connection.Method.POST)
                .execute().cookies();
    }

    @Override
    public List<JournalEntry> fetchJournalEntries() throws IOException {
        Element all_notices_div = jsoup.connect("https://inkbunny.net/portal.php")
                .cookies(cookies)
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .get()
                .select("#current_notices_all").get(0);
        ArrayList<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
        for (Element element : all_notices_div.children()) {
            if (isAJournalNotice(element)) {
                journalEntries.add(new JournalEntry(element.select("a.widget_userNameSmall").text(), element.select(".up_notice_title").text()));
            }
        }
        return journalEntries;
    }

    private boolean isAJournalNotice(Element element) {
        return element.id().contains("notice_journals");
    }

    @Override
    public boolean isWatchedUser(String username) {
        return watchedUsernames.contains(username.toLowerCase());
    }
}
