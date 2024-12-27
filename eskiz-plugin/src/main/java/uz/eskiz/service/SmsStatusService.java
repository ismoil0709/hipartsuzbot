package uz.eskiz.service;

import uz.eskiz.dto.request.CommonResultData;
import uz.eskiz.dto.response.smsInfo.SmsDataDTO;
import uz.eskiz.dto.response.smsInfo.SmsMessageDTO;

public interface SmsStatusService {

    CommonResultData<SmsMessageDTO> getSmsStatusById(Long smsId);

    CommonResultData<SmsDataDTO> getDispatchStatusByDispatchId(String dispatchId);
}
