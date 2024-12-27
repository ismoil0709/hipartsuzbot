package uz.eskiz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import uz.eskiz.dto.request.CommonResultData;
import uz.eskiz.dto.response.ReportByMonthResponseDTO;
import uz.eskiz.dto.response.SentSmsReportResponseDTO;
import uz.eskiz.dto.response.smsInfo.SmsResponseDTO;
import uz.eskiz.service.ReportService;
import uz.eskiz.storage.TokenStorage;
import uz.eskiz.util.HelperUtil;

import java.util.List;
import java.util.Optional;

import static uz.eskiz.enums.Constants.AUTHORIZATION_HEADER;
import static uz.eskiz.enums.Constants.BEARER_TOKEN;
import static uz.eskiz.enums.Constants.GET_REPORT_BY_YEAR;
import static uz.eskiz.enums.Constants.GET_REPORT_TOTAL_BY_YEAR_MONTH;
import static uz.eskiz.util.HelperUtil.getInfoFormData;
import static uz.eskiz.util.HelperUtil.isYearAndMonthValid;

@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final WebClient webClient;
    private final TokenStorage tokenStorage;

    @Override
    public CommonResultData<List<SentSmsReportResponseDTO>> getReportByYearAndByMonth(Integer year, Integer month) {
        log.info("## Attempting to report by year: {} and month: {} ##", year, month);
        if (!isYearAndMonthValid(year, month))
            return CommonResultData.failed("Year or month invalid");
        try {
            return fetchReport(year, month)
                    .map(CommonResultData::success)
                    .orElseGet(() -> {
                        log.warn("No report found for year: {} and month: {}", year, month);
                        return CommonResultData.failed("No report found.");
                    });
        } catch (RuntimeException e) {
            log.error("WebClientException occurred while retrieving report: {}", e.getMessage());
            return CommonResultData.failed("Failed to retrieve report: " + e.getMessage());
        }
    }

    @Override
    public CommonResultData<List<ReportByMonthResponseDTO>> getReportByYear(Integer year) {
        log.info("## Attempting to retrieve total by month report for year: {} ##", year);
        try {
            return getTotalByMonthReport(year)
                    .map(CommonResultData::success)
                    .orElseGet(() -> {
                        log.warn("No total by month report found for year: {}", year);
                        return CommonResultData.failed("No total by month report found.");
                    });
        } catch (RuntimeException e) {
            log.error("WebClientException occurred while retrieving total by month report: {}", e.getMessage());
            return CommonResultData.failed("Failed to retrieve total by month report: " + e.getMessage());
        }
    }

    private Optional<List<SentSmsReportResponseDTO>> fetchReport(Integer year, Integer month) {
        return Optional.ofNullable(webClient.post()
                .uri(GET_REPORT_TOTAL_BY_YEAR_MONTH)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN + getToken())
                .body(BodyInserters.fromFormData(getInfoFormData(year, month)))
                .retrieve()
                .onStatus(HttpStatusCode::isError, HelperUtil::handleErrorResponse)
                .bodyToMono(new ParameterizedTypeReference<SmsResponseDTO<List<SentSmsReportResponseDTO>>>() {
                })
                .map(SmsResponseDTO::getData)
                .block());
    }

    private Optional<List<ReportByMonthResponseDTO>> getTotalByMonthReport(Integer year) {
        return Optional.ofNullable(webClient.get()
                .uri(GET_REPORT_BY_YEAR + year)
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN + getToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, HelperUtil::handleErrorResponse)
                .bodyToMono(new ParameterizedTypeReference<SmsResponseDTO<List<ReportByMonthResponseDTO>>>() {
                })
                .map(SmsResponseDTO::getData)
                .block());
    }

    private String getToken() {
        return tokenStorage.getToken();
    }
}