package uz.eskiz.service;

import uz.eskiz.dto.request.CommonResultData;
import uz.eskiz.dto.request.SendSmsDispatchDTO;
import uz.eskiz.dto.response.SendSmsResponseDTO;
import uz.eskiz.dto.response.SendSmsResponseDispatchDTO;

public interface SendSmsService {

    CommonResultData<SendSmsResponseDTO> sendSms(String phone, String text);

    CommonResultData<SendSmsResponseDispatchDTO> sendSmsDispatch(SendSmsDispatchDTO request);
}
