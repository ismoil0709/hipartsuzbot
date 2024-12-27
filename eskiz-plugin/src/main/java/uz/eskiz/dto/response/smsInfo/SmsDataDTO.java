package uz.eskiz.dto.response.smsInfo;

import lombok.Data;

import java.util.List;

@Data
public class SmsDataDTO {
    private int current_page;
    private String path;
    private String prev_page_url;
    private String first_page_url;
    private String last_page_url;
    private String next_page_url;
    private int per_page;
    private int last_page;
    private int from;
    private int to;
    private int total;
    private List<SmsMessageDTO> result;
    private List<LinkDTO> links;
}
