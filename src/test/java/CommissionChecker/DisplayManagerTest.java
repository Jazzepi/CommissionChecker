package CommissionChecker;

import org.apache.commons.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DisplayManager.class)
public class DisplayManagerTest {

    @Mock
    Log logMock;
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
