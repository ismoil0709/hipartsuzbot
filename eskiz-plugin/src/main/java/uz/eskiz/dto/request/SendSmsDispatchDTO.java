package uz.eskiz.dto.request;

import lombok.Data;
import uz.eskiz.dto.MessageDto;

import java.util.List;

@Data
public class SendSmsDispatchDTO {
    private List<MessageDto> messages;
    private String from;
    private String dispatch_id;
}
