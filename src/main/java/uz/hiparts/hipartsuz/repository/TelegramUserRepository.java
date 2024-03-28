package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.TelegramUser;


@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser,Long> {
    TelegramUser findByChatId(Long chatId);
}
