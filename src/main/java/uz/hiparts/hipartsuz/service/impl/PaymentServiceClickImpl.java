package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.hiparts.hipartsuz.dto.ClickInvoiceDto;
import uz.hiparts.hipartsuz.dto.ClickInvoiceStatusDto;
import uz.hiparts.hipartsuz.dto.ClickSendInvoiceDto;
import uz.hiparts.hipartsuz.exception.NotFoundException;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.repository.OrderRepository;
import uz.hiparts.hipartsuz.service.PaymentService;

import java.security.MessageDigest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceClickImpl implements PaymentService {

    private final OrderRepository orderRepository;

    private static final Integer serviceId = 36335;

    private static final String BASE_URL = "https://api.click.uz/v2/merchant/invoice/";
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders headers = new HttpHeaders();

    {
        long unixTime = System.currentTimeMillis() / 1000L;
        headers.set("Auth", "44526:" + encryptPasswordToSHA1(unixTime + "iCUfO2CfZkSg") + ":" + unixTime);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public ClickInvoiceDto sendInvoice(Long orderId, String phoneNumber) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException("Order")
        );
        HttpEntity<ClickSendInvoiceDto> entity = new HttpEntity<>(new ClickSendInvoiceDto(
                serviceId,
                order.getTotalPrice().floatValue(),
                phoneNumber,
                orderId.toString()
        ),headers);
        return restTemplate.exchange(
                BASE_URL + "create",
                HttpMethod.POST,
                entity,
                ClickInvoiceDto.class
                ).getBody();
    }

    @Override
    public ClickInvoiceStatusDto checkInvoice(String phoneNumber) {
        HttpEntity<ClickInvoiceStatusDto> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                BASE_URL + "status",
                HttpMethod.GET,
                entity,
                ClickInvoiceStatusDto.class
        ).getBody();
    }

    @SneakyThrows
    private static String encryptPasswordToSHA1(String password) {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] messageDigest = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
