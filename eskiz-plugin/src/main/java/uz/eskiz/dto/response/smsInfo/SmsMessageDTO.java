package uz.eskiz.dto.response.smsInfo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SmsMessageDTO {
    private long id;
    private int user_id;
    private int country_id;
    private int connection_id;
    private int smsc_id;
    private String dispatch_id;
    private String user_sms_id;
    private String request_id;
    private int price;
    private int total_price;
    private boolean is_ad;
    private String nick;
    private String to;
    private String message;
    private int encoding;
    private int parts_count;
    private Map<String, PartsDTO> parts;
    private String status;
    private Map<String, List<String>> smsc_data;
    private String sent_at;
    private String submit_sm_resp_at;
    private String delivery_sm_at;
    private String created_at;
    private String updated_at;
}

