package uz.hiparts.hipartsuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.service.telegramService.TelegramService;

@RestController
@RequestMapping("/api/v1/telegram")
@CrossOrigin("/")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;

    @PostMapping
    public void getUpdates(@RequestBody Update update) {
        if (update.hasMessage()) {
            TelegramUser telegramUser = telegramUserService.getByChatId(update.getMessage().getChatId());
            if (update.getMessage().getText() != null && update.getMessage().getText().equals("/start") || !(telegramUser != null && telegramUserService.getState(update.getMessage().getChatId()).name().startsWith("INPUT")))
                telegramService.handleMessage(update.getMessage());
            else telegramService.handleInput(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            telegramService.handleCallbackQuery(update.getCallbackQuery());
        }
    }
}
