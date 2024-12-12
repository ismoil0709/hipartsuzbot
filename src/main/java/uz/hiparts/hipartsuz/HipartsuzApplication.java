package uz.hiparts.hipartsuz;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uz.hiparts.hipartsuz.model.BotSettings;
import uz.hiparts.hipartsuz.repository.BotSettingsRepository;

@SpringBootApplication
public class HipartsuzApplication {

    public static void main(String[] args) {
        SpringApplication.run(HipartsuzApplication.class,args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BotSettingsRepository botSettingsRepository) {
        return args -> {
            if (botSettingsRepository.findById(1L).isEmpty()) {
                botSettingsRepository.save(BotSettings.builder()
                                .currency("1000")
                                .deliveryPrice("1000")
                                .operatorNumber("1000")
                        .build());
            }
        };
    }
}
