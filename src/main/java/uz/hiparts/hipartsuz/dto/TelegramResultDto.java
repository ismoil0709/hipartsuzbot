package uz.hiparts.hipartsuz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;

@AllArgsConstructor
@Getter
public class TelegramResultDto {
    private boolean ok;
    private Message result;
}
