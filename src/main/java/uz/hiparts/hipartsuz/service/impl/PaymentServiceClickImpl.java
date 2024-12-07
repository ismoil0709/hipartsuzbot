package uz.hiparts.hipartsuz.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.hiparts.hipartsuz.dto.ClickDto;
import uz.hiparts.hipartsuz.exception.NotFoundException;
import uz.hiparts.hipartsuz.model.ClickPayment;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.repository.ClickPaymentRepository;
import uz.hiparts.hipartsuz.repository.OrderRepository;
import uz.hiparts.hipartsuz.service.PaymentService;

import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PaymentServiceClickImpl implements PaymentService<ClickDto> {

    private final OrderRepository orderRepository;
    private final ClickPaymentRepository clickPaymentRepository;

    private static final String CLICK_INVOICE_URL = "https://api.click.uz/v2/merchant";
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders headers = new HttpHeaders();

    {
        long unixTime = System.currentTimeMillis() / 1000L;
        headers.set("Auth", "44526:" + encryptPasswordToSHA1(unixTime + getSecretKey()) + ":" + unixTime);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public ClickDto prepare(ClickDto dto) {

//        String signKey = DigestUtils.md5Hex(dto.getClickTransId().toString() +
//                dto.getServiceId().toString() +
//                getSecretKey() +
//                dto.getMerchantTransId() +
//                Math.round(dto.getAmount()) +
//                dto.getAction().toString() +
//                dto.getSignTime());
//
//        log.info("Sign key created : {}", signKey);

        ClickPayment payment = new ClickPayment();

        payment.setClickTransId(dto.getClickTransId());
        payment.setAmount(dto.getAmount());

//        if (!dto.getSignString().equals(signKey)) {
//            payment.setError(-1L);
//            payment.setErrorNote("SIGN CHECK FAILED");
//            log.warn("Sign check failed");
//        } else {

            String orderId = dto.getMerchantTransId();

            try {

                Optional<Order> optionalOrder = orderRepository.findById(Long.parseLong(orderId));

                if (optionalOrder.isEmpty()) {
                    payment.setError(-5L);
                    payment.setErrorNote("Order does not exist");
                    log.warn("Order does not exist");
                } else {

                    if (optionalOrder.get().getTotalPrice() == dto.getAmount()) {
                        payment.setError(0L);
                        payment.setErrorNote("SUCCESS");
                        log.info("successfully prepared");
                    } else {
                        payment.setError(-2L);
                        payment.setErrorNote("Incorrect parameter amount");
                        log.warn("Incorrect parameter amount");
                    }
                }
            } catch (Exception e) {
                payment.setError(-5L);
                payment.setErrorNote("Order does not exist");
                log.warn("something went wrong");
            }
//        }

        ClickDto response = new ClickDto();

        if (payment.getError() == 0) {
            payment = clickPaymentRepository.save(payment);
            response.setMerchantPrepareId(payment.getId());
        }

        response.setClickTransId(dto.getClickTransId());
        response.setMerchantTransId(dto.getMerchantTransId());
        response.setError(payment.getError());
        response.setErrorNote(payment.getErrorNote());


        log.warn("Prepare method completed");
        return response;

    }

    @Override
    public ClickDto complete(ClickDto dto) {

//        String signKey = DigestUtils.md5Hex(dto.getClickTransId().toString() +
//                dto.getServiceId().toString() +
//                getSecretKey() +
//                dto.getMerchantTransId() +
//                dto.getMerchantPrepareId().toString() +
//                Math.round(dto.getAmount()) +
//                dto.getAction().toString() +
//                dto.getSignTime());

//        log.info("Sign key created : {}", signKey);

        ClickDto response = new ClickDto();

        response.setClickTransId(dto.getClickTransId());
        response.setMerchantTransId(dto.getMerchantTransId());

//        if (!dto.getSignString().equals(signKey)) {
//            response.setError(-1L);
//            response.setErrorNote("SIGN CHECK FAILED!");
//            log.warn("Sign check failed");
//        } else {

            Optional<ClickPayment> paymentOptional = clickPaymentRepository.findById(dto.getMerchantPrepareId());

            if (paymentOptional.isEmpty()) {
                response.setError(-6L);
                response.setErrorNote("Transactions does not exist");
                log.warn("Transactions does not exist");
            } else {
                try {

                    String orderId = dto.getMerchantTransId();

                    Optional<Order> orderOptional = orderRepository.findById(Long.parseLong(orderId));

                    if (orderOptional.isEmpty()) {
                        response.setError(-5L);
                        response.setErrorNote("Order not found!");
                        log.warn("Order does not exist");
                    } else {
                        Order order = orderOptional.get();

                        if (order.isCancelled()) {
                            response.setError(-9L);
                            response.setErrorNote("Transactions cancelled");
                            log.warn("Transactions cancelled");
                        } else if (order.isPaid()) {
                            response.setError(-4L);
                            response.setErrorNote("Already paid");
                            log.warn("Already paid");
                        } else {
                            if (order.getTotalPrice() == dto.getAmount() && dto.getError() == 0) {

                                order.setPaid(true);
                                response.setError(0L);
                                response.setErrorNote("SUCCESS");

                                orderRepository.save(order);
                                response.setMerchantConfirmId(paymentOptional.get().getId());
                                log.info("Success");
                            } else {
                                response.setError(-2L);
                                response.setErrorNote("Incorrect parameter amount");
                                log.warn("Incorrect parameter amount");
                            }
                        }
                    }
                } catch (Exception e) {
                    response.setError(-9L);
                    response.setErrorNote("Transactions cancelled");
                    log.warn("Transactions cancelled");
                }
            }

//        }

        return response;
    }

    @Override
    public void sendInvoice(Long orderId, String phoneNumber) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException("Order")
        );
        HttpEntity<ClickInvoiceDto> entity = new HttpEntity<>(
                ClickInvoiceDto.builder()
                        .serviceId(getServiceId())
                        .amount(order.getTotalPrice().floatValue())
                        .phoneNumber(phoneNumber)
                        .merchantTransId(orderId.toString())
                        .build(), headers);

        ClickInvoiceDto body = restTemplate.exchange(
                CLICK_INVOICE_URL + "/invoice/create",
                HttpMethod.POST,
                entity,
                ClickInvoiceDto.class
        ).getBody();

        assert body != null;
        order.setInvoiceId(body.getInvoiceId().toString());

        orderRepository.save(order);

    }

    @Override
    public boolean checkInvoice(String phoneNumber) {
        HttpEntity<ClickInvoiceDto> entity = new HttpEntity<>(headers);

        Order order = orderRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(Order::new);

        ClickInvoiceDto body = restTemplate.exchange(
                CLICK_INVOICE_URL + "/payment/status_by_mti/" + getServiceId() + "/" + order.getId(),
                HttpMethod.GET,
                entity,
                ClickInvoiceDto.class
        ).getBody();

        assert body != null;

        return body.getPaymentStatus() == 0 && order.isPaid();
    }


    public Integer getServiceId() {
        return 28003;
    }

    public String getSecretKey() {
        return "iCUfO2CfZkSg";
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

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ClickInvoiceDto {
        @JsonProperty("service_id")
        private Integer serviceId;
        private Float amount;
        @JsonProperty("phone_number")
        private String phoneNumber;
        @JsonProperty("merchant_trans_id")
        private String merchantTransId;
        @JsonProperty("error_code")
        private Integer errorCode;
        @JsonProperty("error_note")
        private String errorNote;
        @JsonProperty("invoice_id")
        private Long invoiceId;
        @JsonProperty("payment_id")
        private Long paymentId;
        @JsonProperty("payment_status")
        private Integer paymentStatus;
    }

}
