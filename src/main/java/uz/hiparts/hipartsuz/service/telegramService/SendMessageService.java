package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.hiparts.hipartsuz.dto.AddressDto;
import uz.hiparts.hipartsuz.model.Branch;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.enums.Callback;
import uz.hiparts.hipartsuz.model.enums.LangFields;
import uz.hiparts.hipartsuz.service.LangService;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.util.BotUtils;
import uz.hiparts.hipartsuz.util.KeyboardUtils;

import java.util.ArrayList;
import java.util.List;

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
                            KeyboardUtils.inlineButton("\uD83C\uDDF7\uD83C\uDDFA Russian", Callback.LANG_RU.getCallback()),
                            KeyboardUtils.inlineButton("\uD83C\uDDFA\uD83C\uDDFF O'zbek tili", Callback.LANG_UZ.getCallback()),
                            KeyboardUtils.inlineButton("\uD83C\uDDFA\uD83C\uDDF8 English",Callback.LANG_EN.getCallback())
                    ))
                    .build();
    }
    public EditMessageText changeLang(TelegramUser telegramUser, Integer messageId){
        return EditMessageText.builder()
                .messageId(messageId)
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.CHOOSE_LANGUAGE,telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton("\uD83C\uDDF7\uD83C\uDDFA Russian", Callback.LANG_RU.getCallback()),
                        KeyboardUtils.inlineButton("\uD83C\uDDFA\uD83C\uDDFF O'zbek tili", Callback.LANG_UZ.getCallback()),
                        KeyboardUtils.inlineButton("\uD83C\uDDFA\uD83C\uDDF8 English",Callback.LANG_EN.getCallback())
                ))
                .build();
    }
    public SendMessage changeLang(Long chatId){
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.LANGUAGE_CHANGED,chatId))
                .chatId(chatId)
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_CHANGE_LANGUAGE,chatId),Callback.CHANGE_LANGUAGE.getCallback())
                ))
                .build();
    }
    public SendMessage start(TelegramUser telegramUser){
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.PHONE_NUMBER,telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.markup(
                        KeyboardUtils.button(
                                langService.getMessage(LangFields.BUTTON_CONTACT,telegramUser.getChatId()),
                                    true,false
                        )
                ))
                .chatId(telegramUser.getChatId())
                .build();
    }

    public void setLang(String data,Integer messageId,TelegramUser telegramUser) {
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
                                KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS,telegramUser.getChatId()),false,false),
                                KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER,telegramUser.getChatId()),false,false)
                        ))
                        .build()
        );
    }

    public SendMessage sendCatalog(TelegramUser telegramUser){
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.CATALOG_MESSAGE,telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButtonWithWebApp(langService.getMessage(LangFields.BUTTON_CATALOG,telegramUser.getChatId()),"https://hipartsuz-front.vercel.app/")
                ))
                .build();
    }

    public SendMessage sendPhoneNumber(String phoneNumber,TelegramUser telegramUser) {
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.USER_PHONE_NUMBER,telegramUser.getChatId()) + " " + phoneNumber)
                .chatId(telegramUser.getChatId())
                .replyMarkup(KeyboardUtils.markup(
                        KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS,telegramUser.getChatId()),false,false),
                        KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER,telegramUser.getChatId()),false,false)
                ))
                .build();
    }
    public void askDeliveryLocation(TelegramUser telegramUser,Integer messageId){
        BotUtils.send(DeleteMessage.builder()
                        .messageId(messageId)
                        .chatId(telegramUser.getChatId())
                .build());
        BotUtils.send(SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.INPUT_SHIPPING_ADDRESS, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.markup(
                        KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_LOCATION,telegramUser.getChatId()),false,true)
                ))
                .build());
    }

    public EditMessageText sendBranches(List<Branch> branches, Integer messageId, TelegramUser telegramUser) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (Branch branch : branches) {
            buttons.add(KeyboardUtils.inlineButton(branch.getName(), Callback.BRANCH.getCallback() + branch.getId()));
        }
        return EditMessageText.builder()
                .replyMarkup(KeyboardUtils.inlineMarkup(buttons))
                .text(langService.getMessage(LangFields.CHOOSE_BRANCH, telegramUser.getChatId()))
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .build();
    }

    public SendMessage sendAddressDetails(AddressDto addressDto, TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.CONFIRM_ADDRESS, telegramUser.getChatId()) + "\n" + addressDto.getName() + addressDto.getDisplayName())
                .replyMarkup(
                        KeyboardUtils.inlineMarkup(List.of(
                                KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_YES, telegramUser.getChatId()), Callback.LOCATION_CONFIRM_YES.getCallback()),
                                KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_NO, telegramUser.getChatId()), Callback.LOCATION_CONFIRM_NO.getCallback())
                        ))
                )
                .build();
    }

    public SendMessage chooseOrderType(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.DELIVERY_OR_PICKUP,telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_DELIVERY,telegramUser.getChatId()),Callback.DELIVERY.getCallback()),
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_PICKUP,telegramUser.getChatId()),Callback.PICK_UP.getCallback())
                ))
                .build();
    }

    public SendMessage invalidShippingAddress(TelegramUser telegramUser) {
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.INVALID_SHIPPING_ADDRESS,telegramUser.getChatId()))
                .chatId(telegramUser.getChatId())
                .build();
    }

    public void sendLocation(TelegramUser telegramUser,String location, Integer messageId) {
        BotUtils.send(
                DeleteMessage.builder()
                        .chatId(telegramUser.getChatId())
                        .messageId(messageId)
                        .build()
        );
        BotUtils.send(
                SendMessage.builder()
                        .chatId(telegramUser.getChatId())
                        .text(langService.getMessage(LangFields.USER_ADDRESS,telegramUser.getChatId()) + "\n" + location)
                        .replyMarkup(
                                KeyboardUtils.markup(
                                        KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS,telegramUser.getChatId()),false,false),
                                        KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER,telegramUser.getChatId()),false,false)
                                ))
                        .build()
        );
    }

    public SendMessage askConfirmCode(TelegramUser telegramUser) {
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.INPUT_CODE,telegramUser.getChatId()))
                .replyMarkup(
                        KeyboardUtils.markup(
                                KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_CHANGE_PHONE_NUMBER,telegramUser.getChatId()),false,false),
                                KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_RESEND_CODE,telegramUser.getChatId()),false,false)
                        )
                )
                .chatId(telegramUser.getChatId())
                .build();
    }
}
