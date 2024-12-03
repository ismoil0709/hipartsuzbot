package uz.hiparts.hipartsuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uz.hiparts.hipartsuz.dto.ClickDto;
import uz.hiparts.hipartsuz.service.impl.PaymentServiceClickImpl;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceClickImpl paymentServiceClick;

    @PostMapping(value = "/click/prepare",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ClickDto prepareClick(@ModelAttribute ClickDto dto){
        return paymentServiceClick.prepare(dto);
    }


    @PostMapping("/payme/prepare")
    public Map<String,String> preparePayme(){
        return null;
    }

    @PostMapping(value = "/click/complete",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ClickDto completeClick(@ModelAttribute ClickDto dto){
        return paymentServiceClick.complete(dto);
    }

    @PostMapping("/payme/complete")
    public Map<String,String> completePayme(){
        return null;
    }

}