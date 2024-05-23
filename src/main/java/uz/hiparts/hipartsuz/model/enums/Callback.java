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
    REMOVE_ADMIN("remove-admin"),
    ADD_ADMIN("add-admin"),
    SKIP_DESCRIPTION("skip-description"),
    CATEGORY("category-"),
    NEW_CATEGORY("new-category"),
    SKIP_DISCOUNT("skip-discount"),
    SET_BY_USERNAME("admin-username-set"),
    SET_BY_PHONE_NUMBER("admin-phone-number-set"),
    REMOVE_BY_USERNAME("admin-username-remove"),
    REMOVE_BY_PHONE_NUMBER("admin-phone-number-remove"),
    CHANGE_CURRENCY("change-currency"),
    ORDER_CONFIRM_YES("order-confirm-yes"),
    ORDER_CONFIRM_NO("order-confirm-no"),

    BOT_SETTINGS("bot-settings"),;

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
