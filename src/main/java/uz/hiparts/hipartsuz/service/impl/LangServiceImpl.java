package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.enums.LangFields;
import uz.hiparts.hipartsuz.service.LangService;
import uz.hiparts.hipartsuz.service.TelegramUserService;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LangServiceImpl implements LangService {
    private final ResourceBundleMessageSource messageSource;
    private final TelegramUserService telegramUserService;

    @Override
    public String getMessage(LangFields keyword, Long chatId) {
        return messageSource.getMessage(keyword.name(),null,new Locale(telegramUserService.getLang(chatId)));
    }
}
