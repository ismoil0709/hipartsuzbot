package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
public interface PaymentService<DTO> {
    DTO prepare(DTO dto);
    DTO complete(DTO dto);
    void sendInvoice(CallbackQuery callbackQuery);
    boolean checkInvoice(String phoneNumber);
}
