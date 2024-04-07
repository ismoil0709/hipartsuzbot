package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.enums.UserState;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.util.BotUtils;

@Service
@RequiredArgsConstructor
public class TelegramService {
    private final TelegramUserService telegramUserService;
    private final SendMessageService sendMessageService;
    public void handleMessage(Message message){
        if (message.hasText()){
            TelegramUser telegramUser = new TelegramUser();
            String text = message.getText();
            if (text.equals("/start"))
                if (telegramUserService.getByChatId(message.getChatId()) == null) {
                    telegramUser.setChatId(message.getChatId());
                    telegramUser.setLang("uz");
                    telegramUser.setState(UserState.START);
                    telegramUserService.save(telegramUser);
                    BotUtils.send(sendMessageService.firstStart(telegramUser));
                }
                else {
                    telegramUser = telegramUserService.getByChatId(message.getChatId());
                    BotUtils.send(sendMessageService.start(telegramUser));
                }
            if (text.startsWith("/setLang")){
                String lang = message.getText().split("-")[1];
                telegramUserService.setLang(message.getChatId(),lang);
            }
        }
    }
    public void handleCallbackQuery(CallbackQuery callbackQuery){
        String data = callbackQuery.getData();
        TelegramUser telegramUser = telegramUserService.getByChatId(callbackQuery.getMessage().getChatId());
        if (data.startsWith("lang")){
            sendMessageService.setLang(data,callbackQuery.getMessage().getMessageId(),telegramUser);
        }
    }
}
