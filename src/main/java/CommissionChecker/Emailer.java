package CommissionChecker;

import CommissionChecker.logger.Logger;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class Emailer {

    @Resource(name = "checkerProperties")
    private Properties props;
    private Session session;
    @Logger private Log log;

    public void sendEmail(String body) throws MessagingException {
        session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(props.getProperty("mail.username"), props.getProperty("mail.password"));
                    }
                });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(props.getProperty("mail.username")));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(props.getProperty("mail.username")));
        message.setSubject("Commission notification");
        message.setText(body);
        log.info("Sending email" + body);
        Transport.send(message);
    }
}