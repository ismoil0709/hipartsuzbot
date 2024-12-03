package uz.hiparts.hipartsuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.hiparts.hipartsuz.dto.ClickDto;
import uz.hiparts.hipartsuz.service.impl.PaymentServiceClickImpl;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceClickImpl paymentServiceClick;

    @PostMapping(value = "/click/prepare",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ClickDto prepareClick(@RequestBody ClickDto dto){
        return paymentServiceClick.prepare(dto);
    }


    @PostMapping("/payme/prepare")
    public Map<String,String> preparePayme(){
        return null;
    }

    @PostMapping(value = "/click/prepare",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE,MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ClickDto completeClick(@RequestBody ClickDto dto){
        return paymentServiceClick.complete(dto);
    }

    @PostMapping("/payme/complete")
    public Map<String,String> completePayme(){
        return null;
    }

}
