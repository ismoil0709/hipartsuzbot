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
        return paymentServiceClick.prepare(new ClickDto(request));
    }

    @PostMapping(value = "/click/complete",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ClickDto completeClick(@RequestParam Map<String,Object> request){
        return paymentServiceClick.complete(new ClickDto(request));
    }

    @PostMapping("/payme")
    JSONObject payme(@RequestBody PaycomRequestForm requestForm,
                    @RequestHeader(value = "Authorization",required = false) String authorization) {
        return paymentServicePayme.payWithPaycom(requestForm, authorization);
    }
}