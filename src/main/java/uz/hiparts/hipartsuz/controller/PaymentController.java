package uz.hiparts.hipartsuz.controller;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uz.hiparts.hipartsuz.dto.ClickDto;
import uz.hiparts.hipartsuz.dto.json.PaycomRequestForm;
import uz.hiparts.hipartsuz.service.impl.PaymentServiceClick;
import uz.hiparts.hipartsuz.service.impl.PaymentServicePayme;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceClick paymentServiceClick;
    private final PaymentServicePayme paymentServicePayme;

    @PostMapping(value = "/click/prepare",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ClickDto prepareClick(@RequestParam Map<String, Object> request){
        return paymentServiceClick.prepare(mapRequestToDto(request));
    }

    @PostMapping(value = "/click/complete",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ClickDto completeClick(@RequestParam Map<String,Object> request){
        return paymentServiceClick.complete(mapRequestToDto(request));
    }

    @PostMapping("/payme")
    JSONObject payme(@RequestBody PaycomRequestForm requestForm,
                    @RequestHeader(value = "Authorization",required = false) String authorization) {
        return paymentServicePayme.payWithPaycom(requestForm, authorization);
    }

    private ClickDto mapRequestToDto(Map<String, Object> request) {
        ClickDto dto = new ClickDto();

        dto.setClickTransId(request.containsKey("click_trans_id") ? Long.valueOf(request.get("click_trans_id").toString()) : null);
        dto.setServiceId(request.containsKey("service_id") ? Long.valueOf(request.get("service_id").toString()) : null);
        dto.setClickPaydocId(request.containsKey("click_paydoc_id") ? Long.valueOf(request.get("click_paydoc_id").toString()) : null);
        dto.setMerchantTransId(request.containsKey("merchant_trans_id") ? request.get("merchant_trans_id").toString() : null);
        dto.setMerchantPrepareId(request.containsKey("merchant_prepare_id") ? Long.valueOf(request.get("merchant_prepare_id").toString()) : null);
        dto.setMerchantConfirmId(request.containsKey("merchant_confirm_id") ? Long.valueOf(request.get("merchant_confirm_id").toString()) : null);
        dto.setAmount(request.containsKey("amount") ? Float.parseFloat(request.get("amount").toString()) : 0.0f);
        dto.setAction(request.containsKey("action") ? Long.valueOf(request.get("action").toString()) : null);
        dto.setError(request.containsKey("error") ? Long.valueOf(request.get("error").toString()) : null);
        dto.setErrorNote(request.containsKey("error_note") ? request.get("error_note").toString() : null);
        dto.setSignTime(request.containsKey("sign_time") ? request.get("sign_time").toString() : null);
        dto.setSignString(request.containsKey("sign_string") ? request.get("sign_string").toString() : null);

        return dto;
    }


}