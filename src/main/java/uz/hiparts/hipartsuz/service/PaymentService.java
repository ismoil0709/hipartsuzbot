package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.ClickInvoiceDto;
import uz.hiparts.hipartsuz.dto.ClickInvoiceStatusDto;

@Service
public interface PaymentService {
    ClickInvoiceDto sendInvoice(Long orderId,String phoneNumber);
    ClickInvoiceStatusDto checkInvoice(String phoneNumber);
}
