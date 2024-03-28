package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.User;

import java.util.List;

@Service
public interface UserService {
    void save(User user);
    void delete(Long id);
    List<User> getAll();
    User getByChatId(Long chatId);
}
