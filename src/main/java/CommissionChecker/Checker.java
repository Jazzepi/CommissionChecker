package CommissionChecker;

import CommissionChecker.logger.Logger;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Checker {

    private List<JournalEntry> foundCommissionOffers = new ArrayList<JournalEntry>();
    @Resource(name = "commissionKeywords")
    private List<String> commissionKeywords;
    @Logger private Log log;

    public ArrayList<JournalEntry> check(CommissionWebsite commissionWebsite) throws IOException {
        ArrayList<JournalEntry> newOfferJournalEntries = new ArrayList<JournalEntry>();
        if(!commissionWebsite.isLoggedIn()) {
            log.info("Logging in");
            commissionWebsite.login();
        }
        for(JournalEntry element: commissionWebsite.fetchJournalEntries()) {
            log.info("Checking " + element.getUsername() + "'s journal " +  element.getJournalName() + " for commissions");
            if(commissionWebsite.isWatchedUser(element.getUsername()) && isCommissionJournalTitle(element.getJournalName().toLowerCase())) {
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
