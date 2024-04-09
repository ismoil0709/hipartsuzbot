package uz.hiparts.hipartsuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.enums.UserState;
import uz.hiparts.hipartsuz.service.TelegramUserService;

@RequiredArgsConstructor
@RestController("/api/v1/tg-user")
public class TelegramUserController {
    private final TelegramUserService telegramUserService;

    @PostMapping("/save")
    public void save(@Valid @RequestBody TelegramUser telegramUser) {
        telegramUserService.save(telegramUser);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<TelegramUser> getById(@PathVariable Long id) {
        return ResponseEntity.ok(telegramUserService.getByChatId(id));
    }

    @GetMapping("/get/lang/{id}")
    public ResponseEntity<String> getLang(@PathVariable Long id) {
        return ResponseEntity.ok(telegramUserService.getLang(id));
    }

    @PatchMapping("/set/lang/{id}")
    public void setLang(@PathVariable Long id, @RequestParam String lang) {
        telegramUserService.setLang(id, lang);
    }

    @GetMapping("/get/state/{id}")
    public ResponseEntity<UserState> getState(@PathVariable Long id) {
        return ResponseEntity.ok(telegramUserService.getState(id));
    }

    @PatchMapping("/set/state/{id}")
    public void setState(@PathVariable Long id, @Valid @RequestParam UserState userState) {
        telegramUserService.setState(id, userState);
    }
}
