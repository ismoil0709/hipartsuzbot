package uz.hiparts.hipartsuz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.hiparts.hipartsuz.model.enums.UserState;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class TelegramUser {
    @Id
    private Long chatId;
    private UserState state;
    private String lang;
}
