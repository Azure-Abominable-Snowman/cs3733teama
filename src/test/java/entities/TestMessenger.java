package entities;

import com.teama.messages.Provider;
import com.teama.messages.SMSMessage;

import javax.mail.internet.AddressException;

/**
 * Created by jakepardue on 11/13/17.
 */
public class TestMessenger {

    public static void main(String[] args) throws AddressException {
        /*
        SMSMessage m1 = new SMSMessage("7813153422", SMSMessage.Provider.VERIZON,
                "Hello Jake this is IntelliJ");

        m1.sendSMSMessage();*/


        SMSMessage m2 = new SMSMessage("6034893939", Provider.VERIZON, "Hello jake from IntelliJ");
        m2.sendSMSMessage();

    }


}
