package uz.eskiz.service;

import uz.eskiz.dto.request.CommonResultData;
import uz.eskiz.dto.request.TemplateRequestDTO;
import uz.eskiz.dto.response.TemplateResponseDTO;

import java.util.List;

public interface TemplateService {
    CommonResultData<List<TemplateResponseDTO>> getTemplateData();

    CommonResultData<Long> createTemplate(TemplateRequestDTO template);
}
