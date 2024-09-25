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

import java.io.IOException;


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
    public ResourceBundleMessageSource messageSourceResourceBundle() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        resourceBundleMessageSource.setFallbackToSystemLocale(false);
        resourceBundleMessageSource.setDefaultEncoding(CharEncoding.UTF_8);
        return resourceBundleMessageSource;
    }

    @PostConstruct
    public void init() throws IOException {
        if (!BotUtils.getWebhookUrl().equals("https://hipartsbot.uz/api/v1/telegram"))
            BotUtils.send(SetWebhook.builder().url("https://hipartsbot.uz/api/v1/telegram").build());
    }

}
