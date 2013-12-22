package CommissionChecker;

import java.io.IOException;
import java.util.List;

/**
 * Created by Whitekitten on 12/21/13.
 */
public abstract class CommissionWebsite {
    public abstract boolean isLoggedIn() throws IOException;

    public abstract void login() throws IOException;

    public abstract List<JournalEntry> fetchJournalEntries() throws IOException;

    public abstract boolean isWatchedUser(String username);

    public String getName() {
        return getClass().getSimpleName();
    }
}
