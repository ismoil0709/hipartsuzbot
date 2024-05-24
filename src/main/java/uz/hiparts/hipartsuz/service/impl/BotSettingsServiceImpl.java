package uz.hiparts.hipartsuz.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.repository.BotSettingsRepository;
import uz.hiparts.hipartsuz.service.BotSettingsService;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class BotSettingsServiceImpl implements BotSettingsService {
    private final BotSettingsRepository botSettingsRepository;

    @Override
    public String getCurrency() {
        AtomicReference<String> currency = new AtomicReference<>("");
        botSettingsRepository.findById(1L).ifPresent(b-> currency.set(b.getCurrency()));
        return currency.get();
    }

    @Override
    public String getDeliveryPrice() {
        AtomicReference<String> price = new AtomicReference<>("");
        botSettingsRepository.findById(1L).ifPresent(b-> price.set(b.getDeliveryPrice()));
        return price.get();
    }

    @Override
    public String getOperatorNumber() {
        AtomicReference<String> number = new AtomicReference<>("");
        botSettingsRepository.findById(1L).ifPresent(b-> number.set(b.getOperatorNumber()));
        return number.get();
    }

    @Override
    public void setOperatorNumber(String operatorNumber) {
        botSettingsRepository.findById(1L).ifPresent(b->{
            b.setOperatorNumber(operatorNumber);
            botSettingsRepository.save(b);
        });
    }

    @Override
    public void setDeliveryPrice(String deliveryPrice) {
        botSettingsRepository.findById(1L).ifPresent(b->{
            b.setDeliveryPrice(deliveryPrice);
            botSettingsRepository.save(b);
        });
    }

    @Override
    public void setCurrency(String currency) {
        botSettingsRepository.findById(1L).ifPresent(b->{
            b.setCurrency(currency);
            botSettingsRepository.save(b);
        });
    }
}
