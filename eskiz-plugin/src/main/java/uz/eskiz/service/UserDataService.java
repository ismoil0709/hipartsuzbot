package uz.eskiz.service;

import uz.eskiz.dto.request.CommonResultData;
import uz.eskiz.dto.response.UserDataResponseDTO;

public interface UserDataService {
    CommonResultData<UserDataResponseDTO> getUserData();
}
