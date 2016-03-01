package org.mpinnegar.commissionChecker;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of = "link")
@ToString
@Getter
@Accessors(fluent = true)
public class JournalEntry {
    private String username;
    private String journalName;
    private String link;

    JournalEntry(String username, String journalName, String link) {
        this.username = username;
        this.journalName = journalName;
        this.link = link;
    }
}
