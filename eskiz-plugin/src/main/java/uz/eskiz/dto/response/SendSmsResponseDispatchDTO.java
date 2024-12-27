package uz.eskiz.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SendSmsResponseDispatchDTO {
    private String id;
    private String message;
    private List<String> status;
}
