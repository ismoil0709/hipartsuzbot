package uz.hiparts.hipartsuz.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.WebhookInfo;
import uz.hiparts.hipartsuz.dto.TelegramResultDto;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class BotUtils {
    private static final String BASE_URL = "https://api.telegram.org/bot";
    private static final String FILE_URL = "https://api.telegram.org/file/bot";
    private static final String BOT_TOKEN = "6412338008:AAFcXM2MXc2mCSUb3Rv3U1fP_JRY9GA77_A/";
    private static final RestTemplate restTemplate = new RestTemplate();

    public static <T extends Serializable, Method extends BotApiMethod<T>> void send(Method method) {
        restTemplate.postForObject(BASE_URL + BOT_TOKEN + method.getMethod(), method, TelegramResultDto.class);
    }

    public static void send(SendPhoto method) {
        Map<String, String> map = new HashMap<>();
        map.put("chat_id", method.getChatId());
        map.put("photo", method.getFile().getAttachName());
        map.put("caption", method.getCaption());
        HttpEntity<?> requestEntity = new HttpEntity<>(map);
        restTemplate.exchange(BASE_URL + BOT_TOKEN + method.getMethod(), HttpMethod.POST, requestEntity, Void.class);
    }

    @SneakyThrows
    public static String getFile(String fileId) {
            String fileName = UUID.randomUUID() + ".jpg";
            FileResponse response = restTemplate.getForObject(BASE_URL + BOT_TOKEN + "getFile?file_id=" + fileId, FileResponse.class);
            if (response != null && response.isOk()) {
                String filePath = response.getResult().getFilePath();
                byte[] fileBytes = restTemplate.getForObject(FILE_URL + BOT_TOKEN + filePath, byte[].class);
                if (fileBytes != null) {
                    Path path = Path.of("src", "main", "resources", "static", "product_photo", fileName);
                    Files.createFile(path);
                    Files.write(path, fileBytes, StandardOpenOption.CREATE);
                    System.out.println("File downloaded successfully.");
                } else {
                    System.out.println("Failed to download the file.");
                }
            } else {
                System.out.println("Failed to get file path");
            }
            return "https://hipartsbot.uz/api/v1/image/get/" + fileName;
    }

    @Setter
    @Getter
    private static class FileResponse {
        private boolean ok;
        private BotUtils.File result;

    }

    @Getter
    @Setter
    @ToString
    private static class File {
        @JsonProperty("file_id")
        private String fileId;
        @JsonProperty("file_path")
        private String filePath;
    }

    public static String getWebhookUrl() {
        WebHookResult resultDto = restTemplate.getForObject(BASE_URL + BOT_TOKEN + "/getWebhookInfo", WebHookResult.class);
        System.out.println(resultDto);
        if (resultDto != null) {
            if (resultDto.getResult().getUrl() != null) {
                return resultDto.getResult().getUrl();
            }
        }
        return "";
    }

    @AllArgsConstructor
    @Getter
    private static class WebHookResult {
        private boolean ok;
        private WebhookInfo result;
    }
}
