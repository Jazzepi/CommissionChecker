package CommissionChecker;

import CommissionChecker.logger.Logger;
import org.apache.commons.logging.Log;
import org.apache.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

@Component
public class DisplayManager implements StateListener {

    private SystemTray systemTray;
    private final TrayIcon trayIcon;
    Image runningImage;
    Image idleImage;
    @Logger
    private Log log;

    @Autowired
    DisplayManager(Runner runner, @Qualifier("runningImage") Image runningImage, @Qualifier("sleepingImage") Image idleImage, TrayIcon trayIcon) throws IOException, AWTException {
        this.runningImage = runningImage;
        this.idleImage = idleImage;
        this.trayIcon = trayIcon;

        if(SystemTray.isSupported()) {
            systemTray = SystemTray.getSystemTray();
            systemTray.add(trayIcon);
            PopupMenu popupMenu = new PopupMenu();
            MenuItem exitItem = new MenuItem("Exit");
            popupMenu.add(exitItem);
            trayIcon.setPopupMenu(popupMenu);
            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    systemTray.remove(DisplayManager.this.trayIcon);
                    LogManager.shutdown();
                    System.exit(0);
                }
            });
            runner.registerStateListener(this);
        }
    }

    @Override
    public void stateChange(ApplicationState newState) {
        switch (newState) {
            case RUNNING:
                trayIcon.setImage(runningImage);
                break;
            case SLEEPING:
                trayIcon.setImage(idleImage);
                break;
            default:
                throw new IllegalArgumentException("I have no way of handling this state change");
        }
    }
}