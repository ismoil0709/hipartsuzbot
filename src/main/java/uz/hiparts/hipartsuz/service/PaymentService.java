package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;

@Service
public interface PaymentService<DTO> {
    DTO prepare(DTO dto);
    DTO complete(DTO dto);
    void sendInvoice(Long orderId, String phoneNumber);
    boolean checkInvoice(String phoneNumber);
}
