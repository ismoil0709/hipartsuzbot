package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.enums.UserState;

@Service
public interface TelegramUserService {

    void save(TelegramUser telegramUser);

    TelegramUser getByChatId(Long chatId);

    String getLang(Long chatId);

    void setLang(Long chatId,String lang);

    UserState getState(Long chatId);

    void setState(Long chatId,UserState userState);
}
