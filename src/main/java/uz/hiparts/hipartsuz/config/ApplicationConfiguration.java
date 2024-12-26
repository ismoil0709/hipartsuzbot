package uz.hiparts.hipartsuz.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import uz.hiparts.hipartsuz.service.telegramService.BotService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfiguration {

    @Value("${server.port}")
    private int port;

    @Value("${telegram.url}")
    private String url;

    @Value("${spring.profiles.active}")
    private String profile;

    private final BotService botService;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public ResourceBundleMessageSource messageSourceResourceBundle() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        resourceBundleMessageSource.setFallbackToSystemLocale(false);
        resourceBundleMessageSource.setDefaultEncoding(CharEncoding.UTF_8);
        return resourceBundleMessageSource;
    }

    @PostConstruct
    public void init() {

        if (profile.equals("loc"))
            runNgrok();

        if (!botService.getWebhookUrl().equals(url))
            botService.send(SetWebhook.builder().url(url).build());
    }

    public void runNgrok() {
        String ngrokPath = null;
        String command = "which";
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            command = "where";
        }
        try {
            System.out.println(command);
            Process process = new ProcessBuilder(command, "ngrok").start();
            process.waitFor();
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                ngrokPath = reader.readLine();
                reader.close();
            } else {
                log.error("Failed to locate ngrok using 'which ngrok'. Please ensure ngrok is installed and accessible in your system PATH.");
                System.exit(0);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error while finding ngrok path: " + e.getMessage());
            System.exit(0);
        }
        if (ngrokPath == null || url == null || port == 0) {
            log.error("Missing required properties: ngrok_url, server_port, or ngrok not found. Ngrok tunnel creation skipped.");
            System.exit(0);
        } else {
            try {
                Process process = new ProcessBuilder(ngrokPath, "http", "--domain=" + url, String.valueOf(port)).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
