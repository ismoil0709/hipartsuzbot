//package uz.hiparts.hipartsuz.service.telegramService;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SMSService {
//    @Value("${twilio.account.sid}")
//    private String ACCOUNT_SID;
//    @Value("${twilio.account.token}")
//    private String ACCOUNT_TOKEN;
//    @Value("${twilio.account.number}")
//    private String ACCOUNT_OUTING_NUMBER;
//
//    @PostConstruct
//    public void init() {
//        Twilio.init(ACCOUNT_SID, ACCOUNT_TOKEN);
//    }
//
//    public String sendSMS(String phoneNumber) {
//        return Message.creator(
//                new PhoneNumber(phoneNumber),
//                new PhoneNumber(""),
//                "salom"
//        ).create().getStatus().toString();
//    }
//}
