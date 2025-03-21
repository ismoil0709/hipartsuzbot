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
    CONFIRM_LOCATION_YES("location-confirm-yes"),
    CONFIRM_LOCATION_NO("location-confirm-no"),
    CONFIRM_ORDER_YES("order-confirm-yes"),
    CONFIRM_ORDER_NO("order-confirm-no"),
    CONFIRM_PRODUCT_CHANGES("confirm-product-changes"),

    SKIP_DESCRIPTION("skip-description"),
    SKIP_DISCOUNT("skip-discount"),

    BRANCH("branch-"),
    BRANCH_DELETE("branch_delete-"),
    CATALOG("catalog"),
    ADD_PRODUCT("add-product"),
    REMOVE_ADMIN("remove-admin"),
    ADD_ADMIN("add-admin"),
    CATEGORY("category-"),
    CATEGORY_DELETE("category_delete-"),
    NEW_CATEGORY("new-category"),
    CHANGE_NEW_CATEGORY("change-category"),
    SET_BY_USERNAME("admin-username-set"),
    SET_BY_PHONE_NUMBER("admin-phone-number-set"),
    REMOVE_BY_USERNAME("admin-username-remove"),
    REMOVE_BY_PHONE_NUMBER("admin-phone-number-remove"),
    CHANGE_CURRENCY("change-currency"),
    CHANGE_DELIVERY_PRICE("change-delivery-price"),
    CHANGE_OPERATOR_NUMBER("change-operator-number"),
    DELETE_PRODUCT("delete-product"),
    ADD_BRANCH("add-branch"),
    DELETE_BRANCH("delete-branch"),
    ADD_CATEGORY("add-category"),
    DELETE_CATEGORY("delete-category"),
    EXPORT_PRODUCTS("export-products"),

    CHANGE_PRODUCT("change-product"),
    CHANGE_PRODUCT_NAME("product-name"),
    CHANGE_PRODUCT_DESCRIPTION("product-description"),
    CHANGE_PRODUCT_PRICE("product-price"),
    CHANGE_PRODUCT_IMAGE("product-image"),
    CHANGE_PRODUCT_CATEGORY("product-category"),
    CHANGE_PRODUCT_DISCOUNT("product-discount"),
    BACK_TO_ADMIN_PANEL("back-to-admin-panel"),
    BACK_TO_CHANGE_LANG("back-to-change-lang"),
    BACK_TO_MAIN_MENU("back-to-main-menu"),
    BACK_TO_CHOOSE_ORDER_TYPE("back-to-choose-order-type"),
    CHANGED_CATEGORY("changed_category-"),

    BOT_SETTINGS("bot-settings");

    private String callback;
    public static final Map<String, Callback> map = new HashMap<>();
    static {
        for (Callback c: Callback.values()) {
            map.put(c.getCallback(),c);
        }
    }
    public static Callback of(String data){
        return map.get(data);
    }
}
