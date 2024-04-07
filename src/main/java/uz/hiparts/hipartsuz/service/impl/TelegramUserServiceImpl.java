package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.enums.UserState;
import uz.hiparts.hipartsuz.repository.TelegramUserRepository;
import uz.hiparts.hipartsuz.service.TelegramUserService;

@Service
@RequiredArgsConstructor
public class TelegramUserServiceImpl implements TelegramUserService {
    private final TelegramUserRepository telegramUserRepository;

    @Override
    public void save(TelegramUser telegramUser) {
        if (telegramUser.getLang() == null)
            telegramUser.setLang("uz");
        telegramUserRepository.save(telegramUser);
    }

    @Override
    public TelegramUser getByChatId(Long chatId) {
        return telegramUserRepository.findByChatId(chatId).orElseGet(()->
                TelegramUser.builder()
                        .chatId(chatId)
                        .lang("uz")
                        .build()
        );
    }

    @Override
    public String getLang(Long chatId) {
        if (telegramUserRepository.findByChatId(chatId).isPresent())
            return telegramUserRepository.findByChatId(chatId).get().getLang();
        return "uz";
    }

    @Override
    public void setLang(Long chatId, String lang) {
        TelegramUser user = telegramUserRepository.findByChatId(chatId).orElseGet(()->
                TelegramUser.builder()
                        .chatId(chatId)
                        .lang(lang)
                        .build()
                );
        user.setLang(lang);
        telegramUserRepository.save(user);
    }

    @Override
    public UserState getState(Long chatId) {
        if (telegramUserRepository.findByChatId(chatId).isPresent())
            return telegramUserRepository.findByChatId(chatId).get().getState();
        return UserState.DEFAULT;
    }

    @Override
    public void setState(Long chatId, UserState userState) {
        TelegramUser user = telegramUserRepository.findByChatId(chatId).orElseGet(()->
                TelegramUser.builder()
                        .chatId(chatId)
                        .lang("uz")
                        .build()
        );
        user.setState(userState);
        telegramUserRepository.save(user);
    }
}
