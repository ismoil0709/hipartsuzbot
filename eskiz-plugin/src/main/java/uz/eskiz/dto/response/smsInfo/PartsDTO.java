package uz.eskiz.dto.response.smsInfo;

import lombok.Data;

@Data
public class PartsDTO {
    private long group;
    private boolean accepted;
    private String dlr_time;
    private String dlr_state;
    private int part_index;
    private String accept_time;
    private int accept_status;
}

