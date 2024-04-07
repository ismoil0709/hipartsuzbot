package uz.hiparts.hipartsuz.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import uz.hiparts.hipartsuz.dto.TelegramResultDto;

import java.io.Serializable;

@UtilityClass
public class BotUtils {
    private static final String BASE_URL = "https://api.telegram.org/bot";
    private static final String BOT_TOKEN = "6412338008:AAFcXM2MXc2mCSUb3Rv3U1fP_JRY9GA77_A/";
    private static final RestTemplate restTemplate = new RestTemplate();
    public static <T extends Serializable,Method extends BotApiMethod<T>> void send(Method method) {
        restTemplate.postForObject(BASE_URL + BOT_TOKEN + method.getMethod(),method, TelegramResultDto.class);
    }
}
