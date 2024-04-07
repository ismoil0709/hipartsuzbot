package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.enums.LangFields;
import uz.hiparts.hipartsuz.service.LangService;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.util.BotUtils;
import uz.hiparts.hipartsuz.util.KeyboardUtils;

@Service
@RequiredArgsConstructor
public class SendMessageService {
    private final LangService langService;
    private final TelegramUserService telegramUserService;
    public SendMessage firstStart(TelegramUser telegramUser){
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text("Assalomu aleykum! Keling, avvaliga xizmat ko'rsatish tilini tanlab olaylik!")
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton("\uD83C\uDDF7\uD83C\uDDFA Russian","lang-russian"),
                        KeyboardUtils.inlineButton("\uD83C\uDDFA\uD83C\uDDFF O'zbek tili","lang-uzbek"),
                        KeyboardUtils.inlineButton("\uD83C\uDDFA\uD83C\uDDF8 English","lang-english")
                        ))
                .build();
    }
    public SendMessage start(TelegramUser telegramUser){
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.START,telegramUser.getChatId()))
                .chatId(telegramUser.getChatId())
                .build();
    }

    public void setLang(String data,Integer messageId,TelegramUser telegramUser) {
        String lang = data.split("-")[1];
        if (lang.equals("russian"))
            telegramUser.setLang("ru");
        if (lang.equals("uzbek"))
            telegramUser.setLang("uz");
        if (lang.equals("english"))
            telegramUser.setLang("en");
        telegramUserService.save(telegramUser);
        BotUtils.send(
                EditMessageText.builder()
                        .chatId(telegramUser.getChatId())
                        .text(langService.getMessage(LangFields.LANGUAGE_CHANGED, telegramUser.getChatId()))
                        .messageId(messageId)
                        .build()
        );
    }
}
