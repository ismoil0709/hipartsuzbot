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
    PICK_UP("pickup"),
    DELIVERY("delivery"),
    CONFIRM_YES("confirm-yes"),
    CONFIRM_NO("confirm-no"),
    BRANCH("branch-"),
    CATALOG("catalog"),
    LOCATION_CONFIRM_YES("location-confirm-yes"),
    LOCATION_CONFIRM_NO("location-confirm-no"),
    ADD_PRODUCT("add-product"),
    REMOVE_PRODUCT("remove-product"),
    ADD_ADMIN("add-admin"),
    ALL_USERS("show-users");

    private String callback;
    public static final Map<String, Callback> map = new HashMap<>();
    static {
        for (Callback c: Callback.values()) {
            map.put(c.getCallback(),c);
        }
    }
    public static Callback of(String data){
        System.out.println(data);
        return map.get(data);
    }
}
