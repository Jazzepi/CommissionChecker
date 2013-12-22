package CommissionChecker;

import CommissionChecker.logger.Logger;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: xaoc
 * Date: 30.11.11
 * Time: 17:23
 */
@Component
public class Runner {

    @Autowired
    private Checker checker;
    @Autowired
    private Emailer emailer;
    @Resource(name="activeCommissionWebsites")
    private List<CommissionWebsite> commissionWebsites;
    @Logger
    private Log log;
    private List<StateListener> stateListeners = new ArrayList<StateListener>();

    public void run() throws InterruptedException, IOException, AWTException {
        log.info("Commission checker starting");

        //noinspection InfiniteLoopStatement
        while (true) {
            log.info("Checking online");
            for (CommissionWebsite website : commissionWebsites) {
                try {
                    for (JournalEntry element : checker.check(website)) {
                        String emailBody = element.getUsername() + " has posted this journal " + element.getJournalName() + " on website " + website.getName() + "\n";
                        emailer.sendEmail(emailBody);
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
            log.info("Sleeping for 5 minutes.");
            setState(ApplicationState.SLEEPING);
            Thread.sleep(TimeUnit.MINUTES.toMillis(5));
            setState(ApplicationState.RUNNING);
        }
    }

    public void registerStateListener(StateListener stateListener) {
        stateListeners.add(stateListener);
    }

    private void setState(ApplicationState newState) {
        for (StateListener element : stateListeners) {
            element.stateChange(newState);
        }
    }

}