package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uz.eskiz.service.SendSmsService;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.UserState;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.service.UserService;
import uz.hiparts.hipartsuz.util.UtilLists;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SMSService {

    private final BotService botService;
    private final UserService userService;
    private final SendSmsService sendSmsService;
    private final SendMessageService sendMessageService;
    private final TelegramUserService telegramUserService;

    private final Map<Long,String> phoneNumbers = new HashMap<>();
    private final Map<Long,Integer> confirmCodes = new HashMap<>();

    public void send(TelegramUser telegramUser, String phoneNumber) {

        String text = "Kodni hech kimga bermang! Hipartsuz telegram bot ga kirish uchun tasdiqlash kodi: ";

        int code = new Random().nextInt(9000) + 1000;

        phoneNumbers.put(telegramUser.getChatId(),phoneNumber);
        confirmCodes.put(telegramUser.getChatId(),code);

        System.out.println(text);

        sendSmsService.sendSms(phoneNumber,text + code);

        System.out.println(text);

        botService.send(sendMessageService.askConfirmCode(telegramUser));
        telegramUserService.setState(telegramUser.getChatId(),UserState.INPUT_CONFIRM_CODE);

    }

    public void send(TelegramUser telegramUser) {
        send(telegramUser,phoneNumbers.get(telegramUser.getChatId()));
    }

    public boolean check(TelegramUser telegramUser,int code){
        return confirmCodes.get(telegramUser.getChatId()) != null && confirmCodes.get(telegramUser.getChatId()).equals(code);
    }

    public void savePhoneNumber(TelegramUser telegramUser,String phoneNumber) {

        UtilLists.orderMap.put(telegramUser.getChatId(), Order.builder().phoneNumber(phoneNumber).build());

        botService.send(sendMessageService.sendPhoneNumber(phoneNumber,telegramUser));
        botService.send(sendMessageService.chooseOrderType(telegramUser));

        telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
    }

    public void savePhoneNumber(TelegramUser telegramUser) {

        String phoneNumber = phoneNumbers.get(telegramUser.getChatId());

        User user = userService.getByChatId(telegramUser.getChatId());

        user.setLastPhoneNumber(phoneNumber);

        userService.save(user);

        UtilLists.orderMap.put(telegramUser.getChatId(), Order.builder().phoneNumber(phoneNumber).build());

        botService.send(sendMessageService.sendPhoneNumber(phoneNumber,telegramUser));
        botService.send(sendMessageService.chooseOrderType(telegramUser));

        telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);

    }

}
