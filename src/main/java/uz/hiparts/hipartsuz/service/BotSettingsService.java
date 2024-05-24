package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;

@Service
public interface BotSettingsService {
    String getCurrency();
    String getDeliveryPrice();
    String getOperatorNumber();
    void setOperatorNumber(String operatorNumber);
    void setDeliveryPrice(String deliveryPrice);
    void setCurrency(String currency);
}
