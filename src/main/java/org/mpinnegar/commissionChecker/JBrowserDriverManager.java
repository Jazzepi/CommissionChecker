package org.mpinnegar.commissionChecker;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class JBrowserDriverManager {

    @Resource(name = "isHeadless")
    private boolean isHeadless = true;

    public JBrowserDriver getDriver() {
        return new JBrowserDriver(Settings.builder().headless(isHeadless).ajaxResourceTimeout(TimeUnit.SECONDS.toMillis(20)).timezone(Timezone.AMERICA_NEWYORK).build());
    }
}
