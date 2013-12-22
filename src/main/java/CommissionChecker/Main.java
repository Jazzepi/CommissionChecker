package CommissionChecker;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.awt.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: xaoc
 * Date: 30.11.11
 * Time: 17:20
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException, AWTException {
        new AnnotationConfigApplicationContext(AppConfig.class).getBean("runner", Runner.class).run();
    }
}
