package uz.hiparts.hipartsuz.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import uz.hiparts.hipartsuz.dto.TelegramResultDto;

import java.io.Serializable;

@UtilityClass
public class BotUtils {
    private static final String BASE_URL = "https://api.telegram.org/bot";
    private static final String BOT_TOKEN = "6674985300:AAFgJYmO42YggQBFBY1450zkkHmZj7QZGjM/";
    private static final RestTemplate restTemplate = new RestTemplate();
    public static <T extends Serializable,Method extends BotApiMethod<T>> void send(Method method) {
        System.out.println(method.getMethod());
        System.out.println(BASE_URL + BOT_TOKEN + method.getMethod());
        restTemplate.postForObject(BASE_URL + BOT_TOKEN + method.getMethod(),method, TelegramResultDto.class);
    }
}
