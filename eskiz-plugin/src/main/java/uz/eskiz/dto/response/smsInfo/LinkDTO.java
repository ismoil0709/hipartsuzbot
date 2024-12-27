package uz.eskiz.dto.response.smsInfo;

import lombok.Data;

@Data
public class LinkDTO {
    private String url;
    private String label;
    private boolean active;
}

