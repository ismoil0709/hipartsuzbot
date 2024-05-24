package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.exception.NotFoundException;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.Role;
import uz.hiparts.hipartsuz.repository.UserRepository;
import uz.hiparts.hipartsuz.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public void save(User user) {
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

    @Override
    public void setAdminByPhoneNumber(String phoneNumber) {
        if (!(phoneNumber.startsWith("+998"))){
            phoneNumber = "+998" + phoneNumber;
        }
        Optional<User> byPhoneNumber = userRepository.findByLastPhoneNumber(phoneNumber);
        if (byPhoneNumber.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = byPhoneNumber.get();
        if (!(user.getRole() == Role.ADMIN)){
            user.setRole(Role.ADMIN);
        }
        userRepository.save(user);
    }

    @Override
    public void setAdminByUsername(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = byUsername.get();
        if (!(user.getRole() == Role.ADMIN)){
            user.setRole(Role.ADMIN);
        }
        userRepository.save(user);
    }

    @Override
    public void removeAdminByPhoneNumber(String phoneNumber) {
        if (!(phoneNumber.startsWith("+998"))){
            phoneNumber = "+998" + phoneNumber;
        }
        Optional<User> byPhoneNumber = userRepository.findByLastPhoneNumber(phoneNumber);
        if (byPhoneNumber.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = byPhoneNumber.get();
        if (user.getRole() == Role.ADMIN){
            user.setRole(Role.USER);
        }
        userRepository.save(user);
    }

    @Override
    public void removeAdminByUsername(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = byUsername.get();
        if (user.getRole() == Role.ADMIN){
            user.setRole(Role.USER);
        }
        userRepository.save(user);
    }
}
