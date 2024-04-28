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
        for (int i = 0; i < buttons.size(); i++) {
            List<InlineKeyboardButton> row =  new ArrayList<>();
            if (i != buttons.size() -1) {
                row.add(buttons.get(i));
                row.add(buttons.get(i+1));
            }else row.add(buttons.get(i));
            rows.add(row);
        }
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
