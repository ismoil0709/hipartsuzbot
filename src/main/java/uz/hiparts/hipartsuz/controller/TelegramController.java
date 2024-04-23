package uz.hiparts.hipartsuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.enums.Callback;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.service.telegramService.TelegramService;
import uz.hiparts.hipartsuz.util.BotUtils;
import uz.hiparts.hipartsuz.util.KeyboardUtils;

@RestController
@RequestMapping("/api/v1/telegram")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;

    @PostMapping
    public void getUpdates(@RequestBody Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().getText().equals("/salomjon")){
                BotUtils.send(SendMessage.builder()
                                .text("you say salomjon")
                                .chatId(update.getMessage().getChatId())
                                .replyMarkup(KeyboardUtils.inlineMarkup(
                                        KeyboardUtils.inlineButton("Salom", Callback.PICK_UP.getCallback())
                                ))
                        .build());
            }
            TelegramUser telegramUser = telegramUserService.getByChatId(update.getMessage().getChatId());
            if (update.getMessage().getText() != null && update.getMessage().getText().equals("/start") || !(telegramUser != null && telegramUserService.getState(update.getMessage().getChatId()).name().startsWith("INPUT")))
                telegramService.handleMessage(update.getMessage());
            else telegramService.handleInput(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            telegramService.handleCallbackQuery(update.getCallbackQuery());
        }
    }
}
