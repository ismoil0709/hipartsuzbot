package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.Order;

@Service
public interface PaymentService<DTO> {
    DTO prepare(DTO dto);
    DTO complete(DTO dto);
    void sendInvoice(Order order);
    boolean checkInvoice(String phoneNumber);
}
