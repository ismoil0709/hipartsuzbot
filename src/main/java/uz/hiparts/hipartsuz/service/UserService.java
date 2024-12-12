package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.User;

import java.util.List;

@Service
public interface UserService {

    void save(User user);

    List<User> getAll();

    User getByChatId(Long chatId);

    void setAdminByPhoneNumber(String phoneNumber);

    void setAdminByUsername(String username);

    void removeAdminByPhoneNumber(String phoneNumber);

    void removeAdminByUsername(String username);
}
