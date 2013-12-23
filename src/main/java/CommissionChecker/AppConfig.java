package CommissionChecker;

import CommissionChecker.logger.Logger;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Configuration
@ComponentScan(basePackageClasses = AppConfig.class)
public class AppConfig {

    @Logger
    Log log;

    @Bean(name="checkerProperties")
        Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(checkerPropertiesFilename()));
        return properties;
    }

    private String checkerPropertiesFilename() {
        String developmentCheckerPropertiesFileName = System.getenv("dev_checker_properties_file_name");
        return developmentCheckerPropertiesFileName != null ? developmentCheckerPropertiesFileName : "checker.properties";
    }

    @Bean(name="activeCommissionWebsites")
    List<CommissionWebsite> activeCommissionWebsites(@Qualifier("checkerProperties") Properties properties, Inkbunny inkbunny, Furaffinity furaffinity) {
        ArrayList<CommissionWebsite> commissionWebsites = new ArrayList<CommissionWebsite>();
        if(properties.getProperty("site.inkbunny.isActive", "false").equals("true")) {
            commissionWebsites.add(inkbunny);
        }
        if(properties.getProperty("site.furaffinity.isActive", "false").equals("true")) {
            commissionWebsites.add(furaffinity);
        }
        if(commissionWebsites.size() == 0) {
            log.error("No websites are enabled for commission checker. It has no work to do.");
        }
        return commissionWebsites;
    }

    @Bean(name="commissionKeywords")
    List<String> commissionKeywords(@Qualifier("checkerProperties") Properties properties) {
        ArrayList<String> commissionKeywords = new ArrayList<String>();
        Collections.addAll(commissionKeywords, properties.getProperty("commissionKeywords", "").split(","));
        return commissionKeywords;
    }

    @Bean(name="emptyJournalArraylist")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    List<JournalEntry> emptyJournalEntriesArraylist() {
        return new ArrayList<JournalEntry>();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    ArrayList<String> emptyStringArraylist() {
        return new ArrayList<String>();
    }

    @Bean(name="furaffinityUserList")
    List<String> furaffinityUserList(@Qualifier("checkerProperties") Properties properties) {
        ArrayList<String> furaffinityUsernames = new ArrayList<String>();
        for (String element : properties.getProperty("site.furaffinity.watchedUsernames", "").split(",")) {
            furaffinityUsernames.add(element.toLowerCase());
        }
        return furaffinityUsernames;
    }

    @Bean(name="furaffinityUsername")
    String furaffinityUsername(@Qualifier("checkerProperties") Properties properties) {
        return properties.getProperty("site.furaffinity.username","");
    }

    @Bean(name="furaffinityPassword")
    String furaffinityPassword(@Qualifier("checkerProperties") Properties properties) {
        return properties.getProperty("site.furaffinity.password","");
    }

    @Bean(name="inkbunnyUserList")
    List<String> inkbunnyUserList(@Qualifier("checkerProperties") Properties properties) {
        ArrayList<String> inkbunnyUsernames = new ArrayList<String>();
        for (String element : properties.getProperty("site.inkbunny.watchedUsernames", "").split(",")) {
            inkbunnyUsernames.add(element.toLowerCase());
        }
        return inkbunnyUsernames;
    }

    @Bean(name="inkbunnyUsername")
    String inkbunnyUsername(@Qualifier("checkerProperties") Properties properties) {
        return properties.getProperty("site.inkbunny.username","");
    }

    @Bean(name="inkbunnyPassword")
    String inkbunnyPassword(@Qualifier("checkerProperties") Properties properties) {
        return properties.getProperty("site.inkbunny.password","");
    }

    @Bean(name="weasylUserList")
    List<String> weasylUserList(@Qualifier("checkerProperties") Properties properties) {
        ArrayList<String> weasylUsernames = new ArrayList<String>();
        for (String element : properties.getProperty("site.weasyl.watchedUsernames", "").split(",")) {
            weasylUsernames.add(element);
        }
        return weasylUsernames;
    }

    @Bean(name = "emptyStringToStringMap")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Map<String, String> cookies() {
        return new HashMap<String, String>();
    }
}