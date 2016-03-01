package org.mpinnegar.commissionChecker;

import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class Weasyl extends CommissionWebsite{

    private final JSoupWrapper jsoup;
    private final String apiKey;
    @Resource(name = "weasylUserList")
    private List watchedUsernames;

    @Autowired
    Weasyl(JSoupWrapper jsoup, @Qualifier("weasylApiKey") String apiKey) {
        this.jsoup = jsoup;
        this.apiKey = apiKey;
    }

    @Override
    public boolean isLoggedIn() throws IOException {
        return !jsoup.connect("https://www.weasyl.com/")
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .header("X-Weasyl-API-Key", apiKey)
                .get()
                .select("#header-user")
                .isEmpty();
    }

    @Override
    public boolean login() throws IOException {
        //Do nothing implementation. The weasyl API key keeps you "always" logged in.
        return true;
    }

    @Override
    public List<JournalEntry> fetchJournalEntries() throws IOException {
        Elements elements = jsoup.connect("https://www.weasyl.com/messages/notifications")
                .timeout((int) TimeUnit.SECONDS.toMillis(60))
                .header("X-Weasyl-API-Key", apiKey)
                .get()
                .select("h3:containsOwn(Journals) + div.group div.item");

        return elements.parallelStream()
                .map(element -> new JournalEntry(element.select("a.username").text(), element.select("a.username ~ a").text(), null))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isWatchedUser(String username) {
        return watchedUsernames.contains(username.toLowerCase());
    }
}
