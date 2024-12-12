package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;

@Service
public interface BotSettingsService {

    void setCurrency(String currency);

    void setDeliveryPrice(String deliveryPrice);

    void setOperatorNumber(String operatorNumber);

    String getCurrency();

    String getDeliveryPrice();

    String getOperatorNumber();
}
