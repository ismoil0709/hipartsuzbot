package uz.hiparts.hipartsuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uz.hiparts.hipartsuz.dto.ClickDto;
import uz.hiparts.hipartsuz.service.impl.PaymentServiceClickImpl;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceClickImpl paymentServiceClick;

    @PostMapping(value = "/click/prepare",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ClickDto prepareClick(@RequestParam Map<String, Object> request){
        return paymentServiceClick.prepare(mapRequestToDto(request));
    }

    @PostMapping("/payme/prepare")
    public Map<String,String> preparePayme(){
        return null;
    }

    @PostMapping(value = "/click/complete",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ClickDto completeClick(@RequestParam Map<String,Object> request){
        return paymentServiceClick.complete(mapRequestToDto(request));
    }

    @PostMapping("/payme/complete")
    public Map<String,String> completePayme(){
        return null;
    }

    private ClickDto mapRequestToDto(Map<String, Object> request) {
        ClickDto dto = new ClickDto();

        dto.setClickTransId(request.containsKey("click_trans_id") ? Integer.valueOf(request.get("click_trans_id").toString()) : null);
        dto.setServiceId(request.containsKey("service_id") ? Integer.valueOf(request.get("service_id").toString()) : null);
        dto.setClickPaydocId(request.containsKey("click_paydoc_id") ? Integer.valueOf(request.get("click_paydoc_id").toString()) : null);
        dto.setMerchantTransId(request.containsKey("merchant_trans_id") ? request.get("merchant_trans_id").toString() : null);
        dto.setMerchantPrepareId(request.containsKey("merchant_prepare_id") ? Long.valueOf(request.get("merchant_prepare_id").toString()) : null);
        dto.setMerchantConfirmId(request.containsKey("merchant_confirm_id") ? Long.valueOf(request.get("merchant_confirm_id").toString()) : null);
        dto.setAmount(request.containsKey("amount") ? Float.parseFloat(request.get("amount").toString()) : 0.0f);
        dto.setAction(request.containsKey("action") ? Integer.valueOf(request.get("action").toString()) : null);
        dto.setError(request.containsKey("error") ? Integer.valueOf(request.get("error").toString()) : null);
        dto.setErrorNote(request.containsKey("error_note") ? request.get("error_note").toString() : null);
        dto.setSignTime(request.containsKey("sign_time") ? request.get("sign_time").toString() : null);
        dto.setSignString(request.containsKey("sign_string") ? request.get("sign_string").toString() : null);

        return dto;
    }


}