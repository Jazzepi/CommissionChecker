package CommissionChecker;

import CommissionChecker.logger.Logger;
import org.apache.commons.logging.Log;
import org.apache.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

@Component
public class DisplayManager implements StateListener {

    private SystemTray systemTray;
    private TrayIcon trayIcon;
    Image workingImage;
    Image idleImage;
    @Logger
    private Log log;

    @Autowired
    DisplayManager(Runner runner) throws IOException, AWTException {
        if(SystemTray.isSupported()) {
            int trayIconWidth = new TrayIcon(ImageIO.read(getClass().getResource("/working.png"))).getSize().width;
            workingImage = getScaledImage(getClass().getResource("/working.png"), trayIconWidth);
            idleImage = getScaledImage(getClass().getResource("/idle.png"), trayIconWidth);
            trayIcon = new TrayIcon(workingImage);
            systemTray = SystemTray.getSystemTray();
            systemTray.add(trayIcon);
            PopupMenu popupMenu = new PopupMenu();
            MenuItem exitItem = new MenuItem("Exit");
            popupMenu.add(exitItem);
            trayIcon.setPopupMenu(popupMenu);
            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    systemTray.remove(trayIcon);
                    LogManager.shutdown();
                    System.exit(0);
                }
            });
            runner.registerStateListener(this);
        }
    }

    private Image getScaledImage(URL image_location, int width) throws IOException {
        return ImageIO.read(image_location).getScaledInstance(width, -1, Image.SCALE_SMOOTH);
    }

    @Override
    public void stateChange(ApplicationState newState) {
        switch (newState) {
            case RUNNING:
                trayIcon.setImage(workingImage);
                break;
            case SLEEPING:
                trayIcon.setImage(idleImage);
                break;
            default:
                throw new IllegalArgumentException("I have no way of handling this state change");
        }
    }
}