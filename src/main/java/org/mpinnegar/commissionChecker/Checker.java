package org.mpinnegar.commissionChecker;

import org.mpinnegar.commissionChecker.logger.Log;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Checker {

    private List<JournalEntry> foundCommissionOffers = new ArrayList<>();
    @Resource(name = "commissionKeywords")
    private List<String> commissionKeywords;
    @Log
    private Logger log;

    public ArrayList<JournalEntry> check(CommissionWebsite commissionWebsite) throws IOException {
        ArrayList<JournalEntry> newOfferJournalEntries = new ArrayList<>();
        if(!commissionWebsite.isLoggedIn()) {
            log.info("Logging in to" + commissionWebsite.name());
            if (!commissionWebsite.login()) {
                return new ArrayList<>();
            };
        }
        for(JournalEntry element: commissionWebsite.fetchJournalEntries()) {
            log.info("Checking " + element.username() + "'s journal " +  element.journalName() + " for commissions");
            if(commissionWebsite.isWatchedUser(element.username()) && isCommissionJournalTitle(element.journalName().toLowerCase())) {
                if(isAnUnreportedOffer(element)) {
                    log.info("Found new commission offer in this journal" + element);
                    foundCommissionOffers.add(element);
                    newOfferJournalEntries.add(element);
                }
            }
        }
        return newOfferJournalEntries;
    }

    private boolean isAnUnreportedOffer(JournalEntry element) {
        return !foundCommissionOffers.contains(element);
    }

    private boolean isCommissionJournalTitle(String candidate) {
        boolean returnValue = false;
        for(String element: commissionKeywords) {
            if(candidate.contains(element)) {
                returnValue = true;
                break;
            }
        }
        return returnValue;
    }
}
