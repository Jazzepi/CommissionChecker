package org.mpinnegar.commissionChecker;

import org.mpinnegar.commissionChecker.logger.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class Runner {

    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    @Resource(name = "secondsBetweenChecks")
    private int SECONDS_BETWEEN_CHECKS = AppConfig.DEFAULT_SECOND_BETWEEN_CHECKS;
    @Autowired
    private Checker checker;
    @Autowired
    private Emailer emailer;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Resource(name = "activeCommissionWebsites")
    private List<CommissionWebsite> commissionWebsites;
    @Log
    private Logger log;
    private List<StateListener> stateListeners = new ArrayList<>();
    private boolean firstTime = true;

    public void run() throws InterruptedException, IOException, AWTException {
        log.info("Commission checker starting");

        //noinspection InfiniteLoopStatement
        while (true) {
            if (!firstTime) {
                log.info("Waking up.");
            }
            String emailBody = "";
            for (CommissionWebsite website : commissionWebsites) {
                try {
                    for (JournalEntry element : checker.check(website)) {
                        emailBody += element.username() + " has posted this journal " + element.journalName() + " on website " + website.name() + " at page " + element.link() + "\n";
                    }
                } catch (Exception e) {
                    log.error("Failure while checking website [" + website.name() + "]", e);
                }
            }
            if (!emailBody.isEmpty()) {
                emailer.sendEmail(emailBody);
            }
            firstTime = false;
            log.info("Sleeping for " + SECONDS_BETWEEN_CHECKS + " " + TIME_UNIT.name().toLowerCase());
            setState(ApplicationState.SLEEPING);
            Thread.sleep(TIME_UNIT.toMillis(SECONDS_BETWEEN_CHECKS));
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