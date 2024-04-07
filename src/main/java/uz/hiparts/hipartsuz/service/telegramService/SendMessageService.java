package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.enums.LangFields;
import uz.hiparts.hipartsuz.service.LangService;

@Service
@RequiredArgsConstructor
public class SendMessageService {
    private final LangService langService;
    public SendMessage start(TelegramUser telegramUser){
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.START,telegramUser.getChatId()))
                .build();
    }
}
