package uz.hiparts.hipartsuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.enums.OrderType;
import uz.hiparts.hipartsuz.model.enums.PaymentType;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.service.telegramService.SendMessageService;
import uz.hiparts.hipartsuz.service.telegramService.TelegramService;
import uz.hiparts.hipartsuz.util.BotUtils;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/telegram")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramService telegramService;
    private final TelegramUserService telegramUserService;
    @PostMapping
    public void getUpdates(@RequestBody Update update) {
        if (update.hasMessage()) {
            TelegramUser telegramUser = telegramUserService.getByChatId(update.getMessage().getChatId());
            if (update.getMessage().getText() != null && telegramService.isOverrideCommand(update.getMessage()) || !(telegramUser != null && telegramUserService.getState(update.getMessage().getChatId()).name().startsWith("INPUT")))
                telegramService.handleMessage(update.getMessage());
            else telegramService.handleInput(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            telegramService.handleCallbackQuery(update.getCallbackQuery());
        }
    }
}
