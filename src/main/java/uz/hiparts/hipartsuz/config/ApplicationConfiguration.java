package uz.hiparts.hipartsuz.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import uz.hiparts.hipartsuz.util.BotUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfiguration {
    private final Environment env;
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
    public ResourceBundleMessageSource messageSourceResourceBundle(){
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        resourceBundleMessageSource.setFallbackToSystemLocale(false);
        resourceBundleMessageSource.setDefaultEncoding(CharEncoding.UTF_8);
        return resourceBundleMessageSource;
    }
    @PostConstruct
    public void init() throws IOException {
        String ngrokUrl = env.getProperty("bot.data.ngrok_url");
        String serverPort = env.getProperty("server.port");
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
        if (ngrokPath == null || ngrokUrl == null || serverPort == null) {
            log.error("Missing required properties: ngrok_url, server_port, or ngrok not found. Ngrok tunnel creation skipped.");
            System.exit(0);
        }else {
            Process process = new ProcessBuilder(ngrokPath,"http","--domain=" + ngrokUrl,serverPort).start();
            if (!BotUtils.getWebhookUrl().equals("https://" + ngrokUrl + "/api/v1/telegram"))
                BotUtils.send(SetWebhook.builder().url("https://" + ngrokUrl + "/api/v1/telegram").build());
        }
    }
}
