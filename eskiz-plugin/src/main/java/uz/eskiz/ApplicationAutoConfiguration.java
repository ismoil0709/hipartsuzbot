package uz.eskiz;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uz.eskiz.config.properties.EskizProperties;
import uz.eskiz.service.ReportService;
import uz.eskiz.service.SendSmsService;
import uz.eskiz.service.SmsStatusService;
import uz.eskiz.service.TemplateService;
import uz.eskiz.service.UserDataService;
import uz.eskiz.service.impl.ReportServiceImpl;
import uz.eskiz.service.impl.SendSmsServiceImpl;
import uz.eskiz.service.impl.SmsStatusServiceImpl;
import uz.eskiz.service.impl.TemplateServiceImpl;
import uz.eskiz.service.impl.UserDataServiceImpl;
import uz.eskiz.storage.TokenStorage;

import static uz.eskiz.enums.Constants.BASE_URL;

@Configuration
@EnableConfigurationProperties(EskizProperties.class)
public class ApplicationAutoConfiguration {

    private final EskizProperties properties;

    public ApplicationAutoConfiguration(EskizProperties properties) {
        this.properties = properties;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    @Bean
    public TokenStorage tokenStorage() {
        return new TokenStorage(webClient(), properties);
    }

    @Bean
    public UserDataService userDataService() {
        return new UserDataServiceImpl(webClient(),tokenStorage());
    }

    @Bean
    public TemplateService templateService() {
        return new TemplateServiceImpl(webClient(),tokenStorage());
    }

    @Bean
    public SmsStatusService smsStatusService() {
        return new SmsStatusServiceImpl(webClient(),tokenStorage());
    }

    @Bean
    public SendSmsService sendSmsService() {
        return new SendSmsServiceImpl(webClient(),tokenStorage(),properties);
    }

    @Bean
    public ReportService reportService(){
        return new ReportServiceImpl(webClient(),tokenStorage());
    }

}

