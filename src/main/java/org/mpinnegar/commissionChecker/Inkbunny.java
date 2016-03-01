package org.mpinnegar.commissionChecker;

import org.mpinnegar.commissionChecker.logger.Log;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
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
import java.util.stream.Collectors;

@Component
public class Inkbunny extends CommissionWebsite {


    private static final String INKBUNNY_HOST = "https://inkbunny.net/";
    private final JSoupWrapper jsoup;
    private final String username;
    private final String password;
    @Resource(name = "inkbunnyUserList")
    private List<String> watchedUsernames;
    private Map<String, String> cookies = new HashMap<>();
    @Log
    private Logger log;

    @Autowired
    Inkbunny(JSoupWrapper jsoup, @Qualifier("inkbunnyUsername") String username, @Qualifier("inkbunnyPassword") String password) {
        this.jsoup = jsoup;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean isLoggedIn() throws IOException {
        log.info("Checking to see if I'm logged in to Inkbunny.");
        return !jsoup.connect(INKBUNNY_HOST + "index.php")
                .cookies(cookies)
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .method(Connection.Method.GET)
                .execute()
                .parse()
                .select(".loggedin_userdetails")
                .isEmpty();
    }

    @Override
    public boolean login() throws IOException {
        log.info("Logging into Inkbunny with username " + username);
        Connection.Response loginPageResponse = jsoup.connect(INKBUNNY_HOST + "login.php")
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .execute();

        cookies = loginPageResponse.cookies();

        String loginToken = loginPageResponse.parse().select("input[name=token]").attr("value");

        Connection.Response loginAttemptResponse = jsoup.connect(INKBUNNY_HOST + "login_process.php")
                .cookies(cookies)
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .data("username", username)
                .data("password", password)
                .data("token", loginToken)
                .header("Origin", "https://inkbunny.net")
                .referrer(INKBUNNY_HOST + "login.php")
                .header("Host", "inkbunny.net")
                .method(Connection.Method.POST)
                .execute();

        cookies = loginAttemptResponse.cookies();

        return loginAttemptResponse.statusCode() == 200;
    }

    @Override
    public List<JournalEntry> fetchJournalEntries() throws IOException {
        Element all_notices_div = jsoup.connect(INKBUNNY_HOST + "portal.php")
                .cookies(cookies)
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .get()
                .select("#current_notices_all").get(0);
        return all_notices_div.children().parallelStream()
                .filter(this::isAJournalNotice)
                .map(
                        element -> new JournalEntry(
                            element.select("a.widget_userNameSmall").text(),
                            element.select(".up_notice_title").text(),
                            INKBUNNY_HOST + element.select(".up_notice_readbuttons > a").attr("href")
                        )
                )
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isAJournalNotice(Element element) {
        return element.id().contains("notice_journals");
    }

    @Override
    public boolean isWatchedUser(String username) {
        return watchedUsernames.contains(username.toLowerCase());
    }
}
