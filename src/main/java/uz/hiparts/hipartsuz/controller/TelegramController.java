package uz.hiparts.hipartsuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.hiparts.hipartsuz.dto.AddToBasketDto;
import uz.hiparts.hipartsuz.dto.OrderDto;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TelegramController {
    @PostMapping("/telegram")
    public void getUpdates(@RequestBody Update update){
    }
    @PostMapping("/basket")
    public void addToBasket(@RequestBody AddToBasketDto addToBasketDto){
    }
    @PostMapping("/order")
    public void order(@RequestBody OrderDto orderDto){
    }
}
