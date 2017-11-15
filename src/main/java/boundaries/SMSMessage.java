package boundaries;

/**
 * Created by jakepardue on 11/13/17.
 */

import javax.mail.internet.AddressException;
import java.util.Map;
import java.util.EnumMap;


public class SMSMessage {



    public static Map<Provider, String> providers = new EnumMap<Provider, String>(Provider.class);

    String phoneNumber;
    Provider provider;
    String msg;
    String workingAddress;
    EmailMessage emailMessage;

    public void setMap(){
        providers.put(Provider.ATT, "@txt.att.net");
        providers.put(Provider.VERIZON, "@vtext.com");
        providers.put(Provider.TMOBILE, "@tmomail.net");
        providers.put(Provider.SPRINT, "@messaging.sprintpcs.com");
    }

    public SMSMessage(String phoneNumber, Provider prov, String msg){
        this.phoneNumber = phoneNumber;
        this.provider = prov;
        this.msg = msg;

        setMap();

        this.workingAddress = phoneNumber + providers.get(prov);
        this.emailMessage = new EmailMessage(workingAddress, msg, "");
    }

    public void sendSMSMessage() throws AddressException {
        emailMessage.sendMail();
    }

}
