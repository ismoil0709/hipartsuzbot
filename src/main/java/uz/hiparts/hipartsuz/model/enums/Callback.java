package uz.hiparts.hipartsuz.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum Callback {
    LANG_UZ("lang-uz"),
    LANG_RU("lang-ru"),
    LANG_EN("lang-en"),
    CHANGE_LANGUAGE("change-language"),
    PICK_UP("pick-up"),
    DELIVERY("delivery"),
    BRANCH("branch-");
    private String callback;
    public static final Map<String, Callback> map = new HashMap<>();
    static {
        for (Callback c: Callback.values()) {
            map.put(c.getCallback(),c);
        }
    }
    public static Callback of(String data){
        Callback callbacks = map.get(data);
        if(callbacks != null){
            return callbacks;
        }
        throw new IllegalArgumentException();
    }
}