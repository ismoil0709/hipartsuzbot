package uz.hiparts.hipartsuz.service;


import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.enums.LangFields;

@Service
public interface LangService {
    String getMessage(LangFields keyword, Long chatId);
}
