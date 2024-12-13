package uz.hiparts.hipartsuz.service.telegramService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.WebhookInfo;
import uz.hiparts.hipartsuz.dto.TelegramResultDto;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class BotService {

    private static final String BASE_URL = "https://api.telegram.org/bot";
    private static final String FILE_URL = "https://api.telegram.org/file/bot";
    private static final RestTemplate restTemplate = new RestTemplate();

    private final String token;

    public BotService(@Value("${telegram.token}") String token) {
        this.token = token;
        System.out.println(this.token);
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> void send(Method method) {
        restTemplate.postForObject(BASE_URL + token + "/" + method.getMethod(), method, TelegramResultDto.class);
    }

    public void send(SendPhoto method) {
        Map<String, String> map = new HashMap<>();
        map.put("chat_id", method.getChatId());
        map.put("photo", method.getFile().getAttachName());
        map.put("caption", method.getCaption());
        HttpEntity<?> requestEntity = new HttpEntity<>(map);
        restTemplate.exchange(BASE_URL + token + "/" + method.getMethod(), HttpMethod.POST, requestEntity, Void.class);
    }

    public void sendFile(Long chatId, String filePath) {
        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_URL + token + "/sendDocument";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("chat_id", chatId);
        body.add("document", new FileSystemResource(filePath));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        System.out.println("File uploaded successfully!");
    }

    public String getFile(String fileId) {
        String fileUrl = "";
        String directoryPath = "/home/user/product_photo";
        Path directory = Path.of(directoryPath);
        String fileName;

        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            BotService.FileResponse response = restTemplate.getForObject(BASE_URL + token + "getFile?file_id=" + fileId, BotService.FileResponse.class);

            if (response != null && response.isOk()) {
                String originalFilePath = response.getResult().getFilePath();
                String fileExtension = originalFilePath.substring(originalFilePath.lastIndexOf('.'));
                fileName = UUID.randomUUID() + fileExtension;

                fileUrl = FILE_URL + token + originalFilePath;
                byte[] fileBytes = restTemplate.getForObject(fileUrl, byte[].class);

                if (fileBytes != null) {
                    Files.write(directory.resolve(fileName), fileBytes, StandardOpenOption.CREATE);
                    System.out.println("File downloaded successfully: " + fileName);
                } else {
                    System.out.println("Failed to download the file.");
                    return fileUrl;
                }
            } else {
                System.out.println("Failed to get file path.");
                return fileUrl;
            }
        } catch (IOException ex) {
            System.err.println("Error occurred: " + ex.getMessage());
            return fileUrl;
        }

        return fileUrl;
    }

    public String getWebhookUrl() {
        BotService.WebHookResult resultDto = restTemplate.getForObject(BASE_URL + token + "/getWebhookInfo", BotService.WebHookResult.class);
        if (resultDto != null) {
            if (resultDto.getResult().getUrl() != null) {
                return resultDto.getResult().getUrl();
            }
        }
        return "";
    }

    @Setter
    @Getter
    private static class FileResponse {
        private boolean ok;
        private File result;
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

    @AllArgsConstructor
    @Getter
    private static class WebHookResult {
        private boolean ok;
        private WebhookInfo result;
    }

}
