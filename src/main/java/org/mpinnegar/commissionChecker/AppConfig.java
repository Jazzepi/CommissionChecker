package org.mpinnegar.commissionChecker;

import org.mpinnegar.commissionChecker.logger.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Configuration
@ComponentScan(basePackageClasses = AppConfig.class)
public class AppConfig {

    public static final int DEFAULT_SECOND_BETWEEN_CHECKS = 300;
    private static final String PROPERTIES_FILE_NAME = "checker.properties";
    private int systemTrayIconWidth = -1;

    @Log
    Logger log;

    @Bean(name = "checkerProperties")
    Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("./" + checkerPropertiesFilename()));
        } catch (FileNotFoundException e) {
            InputStream defaultFileCheckerPropertiesStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(PROPERTIES_FILE_NAME);
                byte[] buf = new byte[2048];
                int r = defaultFileCheckerPropertiesStream.read(buf);
                while(r != -1) {
                    fos.write(buf, 0, r);
                    r = defaultFileCheckerPropertiesStream.read(buf);
                }
            } finally {
                if(fos != null) {
                    fos.close();
                }
            }
            log.error("You need to fill out the " + PROPERTIES_FILE_NAME + " file first. I extracted a copy of " + PROPERTIES_FILE_NAME + " for you to fill out.");
            System.exit(1);
        }

        return properties;
    }

    private String checkerPropertiesFilename() {
        String developmentCheckerPropertiesFileName = System.getenv("dev_checker_properties_file_name");
        return developmentCheckerPropertiesFileName != null ? developmentCheckerPropertiesFileName : PROPERTIES_FILE_NAME;
    }

    @Bean(name = "isHeadless")
    boolean isHeadless(@Qualifier("checkerProperties") Properties properties) {
        return Boolean.valueOf(properties.getProperty("isHeadless", "true"));
    }

    @Bean(name = "secondsBetweenChecks")
    int secondsBetweenChecks(@Qualifier("checkerProperties") Properties properties) {
        return Math.min(Integer.valueOf(properties.getProperty("waitTimeInSeconds" , String.valueOf(DEFAULT_SECOND_BETWEEN_CHECKS))), 60);
    }

    @Bean(name = "activeCommissionWebsites")
    List<CommissionWebsite> activeCommissionWebsites(@Qualifier("checkerProperties") Properties properties, Inkbunny inkbunny, Furaffinity furaffinity, Weasyl weasyl) {
        ArrayList<CommissionWebsite> commissionWebsites = new ArrayList<>();
        if (Boolean.valueOf(properties.getProperty("site.inkbunny.isActive", "false"))) {
            commissionWebsites.add(inkbunny);
        }
        if (Boolean.valueOf(properties.getProperty("site.furaffinity.isActive", "false"))) {
            commissionWebsites.add(furaffinity);
        }
        if (Boolean.valueOf(properties.getProperty("site.weasyl.isActive", "false"))) {
            commissionWebsites.add(weasyl);
        }
        if (commissionWebsites.size() == 0) {
            log.error("No websites are enabled for commission checker. It has no work to do. Shutting down.");
            System.exit(1);
        }
        return commissionWebsites;
    }

    @Bean(name = "commissionKeywords")
    List<String> commissionKeywords(@Qualifier("checkerProperties") Properties properties) {
        ArrayList<String> commissionKeywords = new ArrayList<>();
        Collections.addAll(commissionKeywords, properties.getProperty("commissionKeywords", "").toLowerCase().split(","));
        return commissionKeywords;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    ArrayList<String> emptyStringArraylist() {
        return new ArrayList<>();
    }

    @Bean(name = "furaffinityUserList")
    List<String> furaffinityUserList(@Qualifier("checkerProperties") Properties properties) {
        ArrayList<String> furaffinityUsernames = new ArrayList<>();
        for (String element : properties.getProperty("site.furaffinity.watchedUsernames", "").split(",")) {
            furaffinityUsernames.add(element.toLowerCase());
        }
        return furaffinityUsernames;
    }

    @Bean(name = "furaffinityUsername")
    String furaffinityUsername(@Qualifier("checkerProperties") Properties properties) {
        return properties.getProperty("site.furaffinity.username", "");
    }

    @Bean(name = "furaffinityPassword")
    String furaffinityPassword(@Qualifier("checkerProperties") Properties properties) {
        return properties.getProperty("site.furaffinity.password", "");
    }

    @Bean(name = "inkbunnyUserList")
    List<String> inkbunnyUserList(@Qualifier("checkerProperties") Properties properties) {
        ArrayList<String> inkbunnyUsernames = new ArrayList<>();
        for (String element : properties.getProperty("site.inkbunny.watchedUsernames", "").split(",")) {
            inkbunnyUsernames.add(element.toLowerCase());
        }
        return inkbunnyUsernames;
    }

    @Bean(name = "inkbunnyUsername")
    String inkbunnyUsername(@Qualifier("checkerProperties") Properties properties) {
        return properties.getProperty("site.inkbunny.username", "");
    }

    @Bean(name = "inkbunnyPassword")
    String inkbunnyPassword(@Qualifier("checkerProperties") Properties properties) {
        return properties.getProperty("site.inkbunny.password", "");
    }

    @Bean(name = "weasylUserList")
    List<String> weasylUserList(@Qualifier("checkerProperties") Properties properties) {
        ArrayList<String> weasylUsernames = new ArrayList<>();
        for (String element : properties.getProperty("site.weasyl.watchedUsernames", "").split(",")) {
            weasylUsernames.add(element);
        }
        return weasylUsernames;
    }

    @Bean(name = "weasylApiKey")
    String weasylPassword(@Qualifier("checkerProperties") Properties properties) {
        return properties.getProperty("site.weasyl.apiKey", "");
    }

    @Bean
    TrayIcon startingTrayIcon(@Qualifier("runningImage") Image workingImage) {
        return new TrayIcon(workingImage);
    }

    @Bean(name = "runningImage")
    Image workingImage() throws IOException {
        if (SystemTray.isSupported()) {
            return getScaledImage(getClass().getResource("/running.png"), systemTrayIconWidth());
        } else {
            log.info("System tray not supported on this system, I am not displaying a system icon. You'll need to kill this process manually to end it.");
            return null;
        }
    }

    @Bean(name = "sleepingImage")
    Image idleImage() throws IOException {
        if (SystemTray.isSupported()) {
            return getScaledImage(getClass().getResource("/sleeping.png"), systemTrayIconWidth());
        } else {
            log.info("System tray not supported on this system, I am not displaying a system icon. You'll need to kill this process manually to end it.");
            return null;
        }
    }

    private int systemTrayIconWidth() throws IOException {
        if (systemTrayIconWidth == -1) {
            systemTrayIconWidth = new TrayIcon(ImageIO.read(getClass().getResource("/running.png"))).getSize().width;
        }
        return systemTrayIconWidth;
    }

    private Image getScaledImage(URL image_location, int width) throws IOException {
        return ImageIO.read(image_location).getScaledInstance(width, -1, Image.SCALE_SMOOTH);
    }

}