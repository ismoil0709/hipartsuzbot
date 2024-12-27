package uz.eskiz.dto.response.smsInfo;

import lombok.Data;

@Data
public class SmsResponseDTO<T> {
    private String status;
    private T data;
}
