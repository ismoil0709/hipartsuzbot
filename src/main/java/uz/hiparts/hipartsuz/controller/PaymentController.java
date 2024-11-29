package uz.hiparts.hipartsuz.controller;

import lombok.RequiredArgsConstructor;
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

    @PostMapping("/click/prepare")
    public ClickDto prepareClick(@RequestBody ClickDto dto){
        return paymentServiceClick.prepare(dto);
    }


    @PostMapping("/payme/prepare")
    public Map<String,String> preparePayme(){
        return null;
    }

    @PostMapping("/click/complete")
    public ClickDto completeClick(@RequestBody ClickDto dto){
        return paymentServiceClick.complete(dto);
    }

    @PostMapping("/payme/complete")
    public Map<String,String> completePayme(){
        return null;
    }

}
