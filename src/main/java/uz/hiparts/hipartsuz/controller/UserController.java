package uz.hiparts.hipartsuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.service.UserService;

import java.util.List;

@RestController("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public void register(@Valid @RequestBody User user) {
        userService.save(user);
    }

    @DeleteMapping("/delete")
    public void delete(Long id) {
        userService.delete(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<User> getByChatId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getByChatId(id));
    }
}
