package org.mpinnegar.commissionChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DisplayManager.class)
public class DisplayManagerTest {

    @Mock
    Logger logMock;
    @Mock
    Runner runnerMock;
    @Mock
    Image runningImageMock;
    @Mock
    Image sleepingImageMock;
    @Mock
    TrayIcon trayIconMock;


    @Test
    public void display_manager_does_nothing_if_system_tray_is_not_supported() throws IOException, AWTException {
        mockStatic(SystemTray.class);
        when(SystemTray.isSupported()).thenReturn(false);

        new DisplayManager(runnerMock, null, null, null);

        verifyZeroInteractions(runnerMock);
    }

    @Test
    public void display_manager_changes_the_system_tray_icon_to_the_sleeping_image_when_state_changes_to_sleeping() throws IOException, AWTException {
        mockStatic(SystemTray.class);
        when(SystemTray.isSupported()).thenReturn(true);
        DisplayManager displayManager = new DisplayManager(runnerMock, runningImageMock, sleepingImageMock, trayIconMock);

        displayManager.stateChange(ApplicationState.SLEEPING);

        verify(trayIconMock).setImage(eq(sleepingImageMock));
    }

    @Test
    public void display_manager_changes_the_system_tray_icon_to_the_running_image_when_state_changes_to_running() throws IOException, AWTException {
        mockStatic(SystemTray.class);
        when(SystemTray.isSupported()).thenReturn(true);
        DisplayManager displayManager = new DisplayManager(runnerMock, runningImageMock, sleepingImageMock, trayIconMock);

        displayManager.stateChange(ApplicationState.RUNNING);

        verify(trayIconMock).setImage(eq(runningImageMock));
    }

}
