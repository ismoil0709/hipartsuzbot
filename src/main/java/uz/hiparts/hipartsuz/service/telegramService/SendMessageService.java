package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.hiparts.hipartsuz.dto.AddressDto;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.enums.Callback;
import uz.hiparts.hipartsuz.model.enums.LangFields;
import uz.hiparts.hipartsuz.service.LangService;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.util.BotUtils;
import uz.hiparts.hipartsuz.util.KeyboardUtils;

import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class SendMessageService {
    private final LangService langService;
    private final TelegramUserService telegramUserService;

    public SendMessage firstStart(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text("Assalomu aleykum! Keling, avvaliga xizmat ko'rsatish tilini tanlab olaylik!")
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton("\uD83C\uDDF7\uD83C\uDDFA Russian", Callback.LANG_RU.getCallback()),
                        KeyboardUtils.inlineButton("\uD83C\uDDFA\uD83C\uDDFF O'zbek tili", Callback.LANG_UZ.getCallback()),
                        KeyboardUtils.inlineButton("\uD83C\uDDFA\uD83C\uDDF8 English", Callback.LANG_EN.getCallback())
                ))
                .build();
    }

    public EditMessageText changeLang(TelegramUser telegramUser, Integer messageId) {
        return EditMessageText.builder()
                .messageId(messageId)
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.CHOOSE_LANGUAGE, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton("\uD83C\uDDF7\uD83C\uDDFA Russian", Callback.LANG_RU.getCallback()),
                        KeyboardUtils.inlineButton("\uD83C\uDDFA\uD83C\uDDFF O'zbek tili", Callback.LANG_UZ.getCallback()),
                        KeyboardUtils.inlineButton("\uD83C\uDDFA\uD83C\uDDF8 English", Callback.LANG_EN.getCallback())
                ))
                .build();
    }

    public SendMessage start(TelegramUser telegramUser) {
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.PHONE_NUMBER, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.markup(
                        KeyboardUtils.button(
                                langService.getMessage(LangFields.BUTTON_CONTACT, telegramUser.getChatId()),
                                true, false
                        )
                ))
                .chatId(telegramUser.getChatId())
                .build();
    }

    public void setLang(String data, Integer messageId, TelegramUser telegramUser) {
        String lang = data.split("-")[1];
        if (lang.equals("ru"))
            telegramUser.setLang("ru");
        if (lang.equals("uz"))
            telegramUser.setLang("uz");
        if (lang.equals("en"))
            telegramUser.setLang("en");
        telegramUserService.save(telegramUser);
        BotUtils.send(
                DeleteMessage.builder()
                        .chatId(telegramUser.getChatId())
                        .messageId(messageId)
                        .build()
        );
        BotUtils.send(
                SendMessage.builder()
                        .chatId(telegramUser.getChatId())
                        .text(langService.getMessage(LangFields.LANGUAGE_CHANGED, telegramUser.getChatId()))
                        .replyMarkup(KeyboardUtils.markup(
                                KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS, telegramUser.getChatId()), false, false),
                                KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER, telegramUser.getChatId()), false, false)
                        ))
                        .build()
        );
    }

    public SendMessage sendPhoneNumber(String phoneNumber, TelegramUser telegramUser) {
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.USER_PHONE_NUMBER, telegramUser.getChatId()) + " " + phoneNumber)
                .chatId(telegramUser.getChatId())
                .build();
    }

    public SendMessage askDeliveryLocation(TelegramUser telegramUser){
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.INPUT_SHIPPING_ADDRESS, telegramUser.getChatId()))
                .build();
    }

    public SendMessage sendAddressDetails(AddressDto addressDto, TelegramUser telegramUser) {
            return SendMessage.builder()
                    .chatId(telegramUser.getChatId())
                    .text(langService.getMessage(LangFields.CONFIRM_ADDRESS, telegramUser.getChatId()) + addressDto.getName() + addressDto.getDisplayName())
                    .replyMarkup(
                            KeyboardUtils.inlineMarkup(
                                    KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_YES, telegramUser.getChatId()), Callback.CONFIRM_YES.getCallback()),
                                    KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_NO, telegramUser.getChatId()), Callback.CONFIRM_NO.getCallback())
                            )
                    )
                    .build();
    }
}
