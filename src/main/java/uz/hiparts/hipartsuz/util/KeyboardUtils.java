package uz.hiparts.hipartsuz.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class KeyboardUtils {
    public static InlineKeyboardMarkup inlineMarkup(InlineKeyboardButton... buttons){
        List<List<InlineKeyboardButton>> rows = Arrays.stream(buttons)
                .map(button -> {
                    List<InlineKeyboardButton> row =  new ArrayList<>();
                    row.add(button);
                    return row;
                }).collect(Collectors.toList());
        return new InlineKeyboardMarkup(rows);
    }
    public static InlineKeyboardMarkup inlineMarkup(List<InlineKeyboardButton> buttons){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        if (buttons.size() % 2 == 0){
            for (int i = 0; i < buttons.size(); i+=2) {
                rows.add(List.of(buttons.get(i), buttons.get(i + 1)));
            }
        }else {
            for (int i = 0; i <= buttons.size(); i+=2) {
                if (i + 1 < buttons.size()) {
                    rows.add(List.of(buttons.get(i), buttons.get(i + 1)));
                } else {
                    rows.add(List.of(buttons.get(i)));
                }
            }
        }
        return new InlineKeyboardMarkup(rows);
    }
    public static InlineKeyboardMarkup categoryMarkup(List<InlineKeyboardButton> buttons){
        InlineKeyboardButton keyboardButton = buttons.remove(buttons.size() - 1);
        List<List<InlineKeyboardButton>> rows = inlineMarkup(buttons).getKeyboard();
        rows.add(List.of(keyboardButton));
        return new InlineKeyboardMarkup(rows);
    }
    public static InlineKeyboardButton inlineButton(String text, String callBack){
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBack);
        return button;
    }
    public static InlineKeyboardButton inlineButtonWithWebApp(String text, String webAppUrl){
        return InlineKeyboardButton.builder()
                .text(text)
                .webApp(new WebAppInfo(webAppUrl))
                .build();
    }
    public static KeyboardButton button(String text,boolean contact,boolean location){
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(text);
        keyboardButton.setRequestContact(contact);
        keyboardButton.setRequestLocation(location);
        return keyboardButton;
    }
    public static ReplyKeyboardMarkup markup(KeyboardButton... buttons){

        List<KeyboardRow> rows = Arrays.stream(buttons)
                .map(button -> {
                    KeyboardRow row = new KeyboardRow();
                    row.add(button);
                    return row;
                }).collect(Collectors.toList());

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(rows);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);
        markup.setSelective(true);
        return markup;
    }
}
