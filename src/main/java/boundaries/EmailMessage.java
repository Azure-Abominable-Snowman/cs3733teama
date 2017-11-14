package boundaries;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by jakepardue on 11/14/17.
 */
public class EmailMessage {
        String to;
        String from;
        String msg;

        //optional
        String subject;
        String password;
        MimeMessage message;
        Session s;
        Properties properties;

        public EmailMessage(String to, String msg, String sub){
            this.to = to;
            this.msg = msg;
            this.subject = sub;
            this.from = "cs3733teama@gmail.com";
            this.password = "wilsonwong";


            this.properties = new Properties();
            properties.put("mail.smtp.username", "cs3733teama@gmail.com");
            properties.put("mail.smtp.password", "wilsonwong");
            properties.put("mail.smtp.protocol", "smtp");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.starttls.enable","true");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");

            Authenticator a1 = new javax.mail.Authenticator(){
                //@Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            };

            this.s = Session.getInstance(properties,a1);
            message = new MimeMessage(s);
        }

        public void sendMail() throws AddressException {

            try{
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject(subject);
                message.setText(msg);

                Transport.send(message);
                System.out.println("Message was sent successfully!");
            }
            catch (MessagingException e) {
                e.printStackTrace();
            }

        }
}
