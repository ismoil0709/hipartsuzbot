package uz.eskiz.service;

import uz.eskiz.dto.request.CommonResultData;
import uz.eskiz.dto.response.ReportByMonthResponseDTO;
import uz.eskiz.dto.response.SentSmsReportResponseDTO;

import java.util.List;

public interface ReportService {
    CommonResultData<List<SentSmsReportResponseDTO>> getReportByYearAndByMonth(Integer year, Integer month);

    CommonResultData<List<ReportByMonthResponseDTO>> getReportByYear(Integer year);
}
