package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.util.BotUtils;

@Service
@RequiredArgsConstructor
public class TelegramService {
    private final TelegramUserService telegramUserService;
    private final SendMessageService sendMessageService;
    public void handleMessage(Message message){
        TelegramUser telegramUser = telegramUserService.getByChatId(message.getChatId());
        if (message.hasText()){
            String text = message.getText();
            if (text.equals("/start"))
                BotUtils.send(sendMessageService.start(telegramUser));
            if (text.startsWith("/setLang")){
                String lang = message.getText().split("-")[1];
                telegramUserService.setLang(message.getChatId(),lang);
            }
        }
    }
    public void handleCallbackQuery(CallbackQuery callbackQuery){

    }
}
