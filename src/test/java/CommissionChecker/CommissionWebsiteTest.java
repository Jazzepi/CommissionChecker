package CommissionChecker;

import org.junit.Test;

import java.awt.*;
import java.io.IOException;

public interface CommissionWebsiteTest {
    @Test
    void is_logged_in_succeeds_if_logged_in_details_are_present() throws IOException, AWTException;

    @Test
    void is_logged_in_fails_if_logged_in_details_are_not_present() throws IOException, AWTException;

    @Test
    void returns_all_journals() throws IOException, AWTException;
}
