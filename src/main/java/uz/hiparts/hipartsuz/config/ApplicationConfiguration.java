package uz.hiparts.hipartsuz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public ResourceBundleMessageSource resourceBundleMessageSource(){
        return new ResourceBundleMessageSource();
    }
}
