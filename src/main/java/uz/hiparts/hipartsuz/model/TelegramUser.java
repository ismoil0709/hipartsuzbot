package uz.hiparts.hipartsuz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.hiparts.hipartsuz.model.enums.UserState;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TelegramUser {

    @Id
    private Long chatId;

    @Enumerated(EnumType.STRING)
    private UserState state;

    private String lang;
}
