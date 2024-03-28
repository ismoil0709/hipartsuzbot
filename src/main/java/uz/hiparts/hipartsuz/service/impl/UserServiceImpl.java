package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.Basket;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.repository.UserRepository;
import uz.hiparts.hipartsuz.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public void save(User user) {
        user.setBasket(new Basket());
        if (userRepository.findByChatId(user.getChatId()).isEmpty())
            userRepository.save(user);
    }
    @Override
    public void delete(Long id) {
        if (userRepository.existsById(id))
            userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
    @Override
    public User getByChatId(Long chatId) {
        return userRepository.findByChatId(chatId).orElse(null);
    }
}
