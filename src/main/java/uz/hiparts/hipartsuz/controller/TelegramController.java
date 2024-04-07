package uz.hiparts.hipartsuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.hiparts.hipartsuz.dto.AddToBasketDto;
import uz.hiparts.hipartsuz.dto.OrderDto;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.service.telegramService.TelegramService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramService telegramService;
    @PostMapping("/telegram")
    public void getUpdates(@RequestBody Update update){
        if(update.hasMessage()){
            telegramService.handleMessage(update.getMessage());
        }else if (update.hasCallbackQuery()){
            telegramService.handleCallbackQuery(update.getCallbackQuery());
        }
    }
}
