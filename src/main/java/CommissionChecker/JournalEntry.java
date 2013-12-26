package CommissionChecker;

public class JournalEntry {
    private final String username;
    private final String journalName;

    JournalEntry(String username, String journalName) {
        this.username = username;
        this.journalName = journalName;
    }

    public String username() {
        return username;
    }

    public String journalName() {
        return journalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JournalEntry that = (JournalEntry) o;

        if (journalName != null ? !journalName.equals(that.journalName) : that.journalName != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (journalName != null ? journalName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JournalEntry{" +
                "username='" + username + '\'' +
                ", journalName='" + journalName + '\'' +
                '}';
    }
}
