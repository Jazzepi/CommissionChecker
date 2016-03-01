package org.mpinnegar.commissionChecker;

import java.io.IOException;
import java.util.List;

public abstract class CommissionWebsite {
    public abstract boolean isLoggedIn() throws IOException;

    public abstract boolean login() throws IOException;

    public abstract List<JournalEntry> fetchJournalEntries() throws IOException;

    public abstract boolean isWatchedUser(String username);

    public String name() {
        return getClass().getSimpleName();
    }
}
