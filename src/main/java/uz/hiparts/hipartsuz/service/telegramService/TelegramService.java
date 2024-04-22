package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.hiparts.hipartsuz.dto.AddressDto;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.Callback;
import uz.hiparts.hipartsuz.model.enums.LangFields;
import uz.hiparts.hipartsuz.model.enums.UserState;
import uz.hiparts.hipartsuz.service.LangService;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.service.UserService;
import uz.hiparts.hipartsuz.util.BotUtils;
import uz.hiparts.hipartsuz.util.KeyboardUtils;
import uz.hiparts.hipartsuz.util.Regex;
import uz.hiparts.hipartsuz.util.UtilLists;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TelegramService {
    private final TelegramUserService telegramUserService;
    private final SendMessageService sendMessageService;
    private final LangService langService;
    private final UserService userService;

    public void handleMessage(Message message) {
        if (message.hasText()) {
            TelegramUser telegramUser = new TelegramUser();
            String text = message.getText();

            if (text.equals("/start"))
                if (telegramUserService.getByChatId(message.getChatId()) == null) {
                    telegramUser.setChatId(message.getChatId());
                    telegramUser.setLang("uz");
                    telegramUser.setState(UserState.START);
                    telegramUserService.save(telegramUser);
                    BotUtils.send(sendMessageService.firstStart(telegramUser));
                } else {
                    telegramUser = telegramUserService.getByChatId(message.getChatId());
                    telegramUserService.setState(message.getChatId(), UserState.INPUT_PHONE_NUMBER);
                    BotUtils.send(sendMessageService.start(telegramUser));
                }
            else if (text.equals(langService.getMessage(LangFields.BUTTON_SETTINGS,message.getChatId()))){
                BotUtils.send(SendMessage.builder()
                                .text(langService.getMessage(LangFields.LANGUAGE_CHANGED,message.getChatId()))
                                .replyMarkup(KeyboardUtils.inlineMarkup(
                                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_CHANGE_LANGUAGE,message.getChatId()),"change-language")
                                ))
                        .chatId(message.getChatId())
                        .build());
            }
        }
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        TelegramUser telegramUser = telegramUserService.getByChatId(callbackQuery.getMessage().getChatId());
        if (data.startsWith("lang")) {
            sendMessageService.setLang(data, callbackQuery.getMessage().getMessageId(), telegramUser);
        }
        Callback callback = Callback.of(data);
        switch (callback){
            case CHANGE_LANGUAGE -> BotUtils.send(sendMessageService.changeLang(telegramUser,callbackQuery.getMessage().getMessageId()));
            case DELIVERY -> BotUtils.send(sendMessageService.askDeliveryLocation(telegramUser));
        }
    }

    public void handleInput(Message message) {
        TelegramUser telegramUser = telegramUserService.getByChatId(message.getChatId());
        User user = userService.getByChatId(message.getChatId());
        if (user == null){
            user = User.builder()
                    .chatId(telegramUser.getChatId())
                    .name(message.getFrom().getFirstName())
                    .username(message.getFrom().getUserName())
                    .lastPhoneNumber("")
                    .build();
            userService.save(user);
        }
        switch (telegramUser.getState()) {
            case INPUT_PHONE_NUMBER -> {
                String phoneNumber;
                if (message.hasContact()) {
                    phoneNumber = message.getContact().getPhoneNumber();
                } else {
                    phoneNumber = "+998" + message.getText();
                }
                if (!phoneNumber.matches(Regex.PHONE_NUMBER)){
                    telegramUserService.setState(telegramUser.getChatId(),UserState.INPUT_PHONE_NUMBER);
                    sendMessageService.start(telegramUser);
                }
                if (!Objects.equals(user.getLastPhoneNumber(), phoneNumber)){
                    //todo confirm via sms
                }
                user.setLastPhoneNumber(phoneNumber);
                userService.save(user);
                UtilLists.orderMap.put(message.getChatId(),Order.builder().phoneNumber(phoneNumber).build());
                BotUtils.send(sendMessageService.sendPhoneNumber(phoneNumber,telegramUser));
            }
            case INPUT_LOCATION -> {
                if (message.hasLocation()){
                    Location location = message.getLocation();
                    AddressDto addressDetails = new RestTemplate().getForObject("https://nominatim.openstreetmap.org/reverse?format=json&lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&zoom=155&addressdetails=1", AddressDto.class);
                    if (addressDetails == null){
                        SendMessage.builder()
                                .text(langService.getMessage(LangFields.INVALID_SHIPPING_ADDRESS, telegramUser.getChatId()))
                                .build();
                    }
                    else sendMessageService.sendAddressDetails(addressDetails, telegramUser);
                }
                    else SendMessage.builder()
                        .text(langService.getMessage(LangFields.INVALID_SHIPPING_ADDRESS, telegramUser.getChatId()))
                        .build();
            }
        }
    }
}
