package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.hiparts.hipartsuz.dto.AddressDto;
import uz.hiparts.hipartsuz.dto.ProductDto;
import uz.hiparts.hipartsuz.model.Branch;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.Callback;
import uz.hiparts.hipartsuz.model.enums.LangFields;
import uz.hiparts.hipartsuz.model.enums.OrderType;
import uz.hiparts.hipartsuz.model.enums.Role;
import uz.hiparts.hipartsuz.repository.CategoryRepository;
import uz.hiparts.hipartsuz.repository.OrderRepository;
import uz.hiparts.hipartsuz.service.LangService;
import uz.hiparts.hipartsuz.service.ProductService;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.service.UserService;
import uz.hiparts.hipartsuz.util.KeyboardUtils;
import uz.hiparts.hipartsuz.util.UtilLists;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SendMessageService {
    private final LangService langService;
    private final TelegramUserService telegramUserService;
    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private User user;
    private final CategoryRepository categoryRepository;

    public SendMessage firstStart(TelegramUser telegramUser) {
        Long chatId = telegramUser.getChatId();
        return SendMessage.builder()
                .chatId(chatId)
                .text("Assalomu aleykum! Keling, avvaliga xizmat ko'rsatish tilini tanlab olaylik!")
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_RUSSIAN_LANGUAGE, chatId), Callback.LANG_RU.getCallback()),
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_UZBEK_LANGUAGE, chatId), Callback.LANG_UZ.getCallback()),
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_ENGLISH_LANGUAGE, chatId), Callback.LANG_EN.getCallback())
                ))
                .build();
    }

    public EditMessageText changeLang(TelegramUser telegramUser, Integer messageId) {
        Long chatId = telegramUser.getChatId();
        return EditMessageText.builder()
                .messageId(messageId)
                .chatId(chatId)
                .text(langService.getMessage(LangFields.CHOOSE_LANGUAGE, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_RUSSIAN_LANGUAGE, chatId), Callback.LANG_RU.getCallback()),
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_UZBEK_LANGUAGE, chatId), Callback.LANG_UZ.getCallback()),
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_ENGLISH_LANGUAGE, chatId), Callback.LANG_EN.getCallback())
                ))
                .build();
    }

    public SendMessage changeLang(Long chatId) {
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.LANGUAGE_CHANGED, chatId))
                .chatId(chatId)
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_CHANGE_LANGUAGE, chatId), Callback.CHANGE_LANGUAGE.getCallback())
                ))
                .build();
    }

    public SendMessage start(TelegramUser telegramUser) {
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.INPUT_PHONE_NUMBER, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.markup(
                        KeyboardUtils.button(
                                langService.getMessage(LangFields.BUTTON_CONTACT, telegramUser.getChatId()),
                                true, false
                        )
                ))
                .chatId(telegramUser.getChatId())
                .build();
    }

    public SendMessage setLang(String data, TelegramUser telegramUser) {
        String lang = data.split("-")[1];
        if (lang.equals("ru"))
            telegramUser.setLang("ru");
        if (lang.equals("uz"))
            telegramUser.setLang("uz");
        if (lang.equals("en"))
            telegramUser.setLang("en");
        telegramUserService.save(telegramUser);
        user = userService.getByChatId(telegramUser.getChatId());
        ReplyKeyboardMarkup markup = KeyboardUtils.markup(
                KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS, telegramUser.getChatId()), false, false),
                KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER, telegramUser.getChatId()), false, false)
        );
        if (user != null && user.getRole().equals(Role.ADMIN)) {
            markup = KeyboardUtils.markup(
                    KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS, telegramUser.getChatId()), false, false),
                    KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER, telegramUser.getChatId()), false, false),
                    KeyboardUtils.button("Admin", false, false)
            );
        }

        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.LANGUAGE_CHANGED, telegramUser.getChatId()))
                .replyMarkup(markup)
                .build();

    }

    public SendMessage sendCatalog(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.CATALOG_MESSAGE, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButtonWithWebApp(langService.getMessage(LangFields.BUTTON_CATALOG, telegramUser.getChatId()), "https://autoexpo2024.uz")
                ))
                .build();
    }

    public SendMessage sendPhoneNumber(String phoneNumber, TelegramUser telegramUser) {
        user = userService.getByChatId(telegramUser.getChatId());
        ReplyKeyboardMarkup markup;
        if (user.getRole().equals(Role.ADMIN)) {
            markup = KeyboardUtils.markup(
                    KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS, telegramUser.getChatId()), false, false),
                    KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER, telegramUser.getChatId()), false, false),
                    KeyboardUtils.button("Admin", false, false)
            );
        } else {
            markup = KeyboardUtils.markup(
                    KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS, telegramUser.getChatId()), false, false),
                    KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER, telegramUser.getChatId()), false, false)
            );
        }
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.USER_PHONE_NUMBER, telegramUser.getChatId()) + " " + phoneNumber)
                .chatId(telegramUser.getChatId())
                .replyMarkup(markup)
                .build();
    }

    public SendMessage askDeliveryLocation(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.INPUT_SHIPPING_ADDRESS, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.markup(
                        KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_LOCATION, telegramUser.getChatId()), false, true)
                ))
                .build();
    }

    public EditMessageText sendBranches(List<Branch> branches, Integer messageId, TelegramUser telegramUser) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (Branch branch : branches) {
            buttons.add(KeyboardUtils.inlineButton(branch.getName(), Callback.BRANCH.getCallback() + branch.getId()));
        }
        return EditMessageText.builder()
                .replyMarkup(KeyboardUtils.inlineMarkup(buttons))
                .text(langService.getMessage(LangFields.CHOOSE_BRANCH, telegramUser.getChatId()))
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .build();
    }

    public SendMessage sendAddressDetails(AddressDto addressDto, TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.CONFIRM_ADDRESS, telegramUser.getChatId()) + "\n" + addressDto.getName() + addressDto.getDisplayName())
                .replyMarkup(
                        KeyboardUtils.inlineMarkup(List.of(
                                KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_YES, telegramUser.getChatId()), Callback.LOCATION_CONFIRM_YES.getCallback()),
                                KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_NO, telegramUser.getChatId()), Callback.LOCATION_CONFIRM_NO.getCallback())
                        ))
                )
                .build();
    }

    public SendMessage chooseOrderType(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.DELIVERY_OR_PICKUP, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_DELIVERY, telegramUser.getChatId()), Callback.DELIVERY.getCallback()),
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_PICKUP, telegramUser.getChatId()), Callback.PICK_UP.getCallback())
                ))
                .build();
    }

    public SendMessage invalidShippingAddress(TelegramUser telegramUser) {
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.INVALID_SHIPPING_ADDRESS, telegramUser.getChatId()))
                .chatId(telegramUser.getChatId())
                .build();
    }

    public SendMessage sendLocation(TelegramUser telegramUser, String location) {
        user = userService.getByChatId(telegramUser.getChatId());
        ReplyKeyboardMarkup markup;
        if (user.getRole().equals(Role.ADMIN)) {
            markup = KeyboardUtils.markup(
                    KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS, telegramUser.getChatId()), false, false),
                    KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER, telegramUser.getChatId()), false, false),
                    KeyboardUtils.button("Admin", false, false)
            );
        } else {
            markup = KeyboardUtils.markup(
                    KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS, telegramUser.getChatId()), false, false),
                    KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER, telegramUser.getChatId()), false, false)
            );
        }
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.USER_ADDRESS, telegramUser.getChatId()) + "\n" + location)
                .replyMarkup(markup)
                .build();
    }

    public SendMessage askConfirmCode(TelegramUser telegramUser) {
        return SendMessage.builder()
                .text(langService.getMessage(LangFields.INPUT_CODE, telegramUser.getChatId()))
                .replyMarkup(
                        KeyboardUtils.markup(
                                KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_CHANGE_PHONE_NUMBER, telegramUser.getChatId()), false, false),
                                KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_RESEND_CODE, telegramUser.getChatId()), false, false)
                        )
                )
                .chatId(telegramUser.getChatId())
                .build();
    }

    public SendMessage sendOrderDetails(Order order) {
        Long chatId = order.getUser().getChatId();
        StringBuilder sb = new StringBuilder(langService.getMessage(LangFields.ORDER_INFO, chatId) + "\n\n");
        if (order.getOrderType() == OrderType.DELIVERY)
            sb.append(langService.getMessage(LangFields.DELIVERY_ADDRESS, chatId)).append(" : ").append(order.getAddress()).append("\n\n");
        else {
            sb.append(langService.getMessage(LangFields.CHOSEN_BRANCH, chatId)).append(" : ").append(order.getBranch()).append("\n\n");
            sb.append(langService.getMessage(LangFields.TIME_RECEIVE_ORDER, chatId)).append(" : ").append(order.getTime()).append("\n\n");
        }
        order.getProductQuantities().forEach(p -> {
            ProductDto product = productService.getById(p.getProductId());
            sb.append(product.getName())
                    .append("\n")
                    .append(p.getQuantity())
                    .append(" x ")
                    .append(Math.round(product.getPrice()))
                    .append(" = ")
                    .append(Math.round(p.getQuantity() * product.getPrice()))
                    .append("\n\n");
        });
        if (order.getOrderType() == OrderType.DELIVERY) {
            sb.append(langService.getMessage(LangFields.DELIVERY, chatId)).append("\n\n1 x 20000 = 20000\n\n");
            sb.append(langService.getMessage(LangFields.DELIVERY_PRICE, chatId)).append(" : ").append("20000\n");
        }
        sb.append(langService.getMessage(LangFields.PAYMENT_TYPE, chatId)).append(" : ").append(order.getPaymentType()).append("\n");
        sb.append(langService.getMessage(LangFields.TOTAL_PRICE, chatId)).append(" : ").append(Math.round(order.getTotalPrice()));
        return SendMessage.builder()
                .text(sb.toString())
                .chatId(chatId)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    public DeleteMessage deleteMessage(Long chatId, Integer messageId) {
        return DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }

    public SendMessage welcomeAdmin(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.WELCOME_ADMIN, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.markup(
                        KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_SETTINGS, telegramUser.getChatId()), false, false),
                        KeyboardUtils.button(langService.getMessage(LangFields.BUTTON_NEW_ORDER, telegramUser.getChatId()), false, false),
                        KeyboardUtils.button("Admin", false, false)
                ))
                .build();
    }

    public SendMessage adminPanel(TelegramUser telegramUser) {
        Long chatId = telegramUser.getChatId();
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_ADD_PRODUCT, chatId), Callback.ADD_PRODUCT.getCallback())))
                .keyboardRow(
                        List.of(
                                KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_ADD_ADMIN, chatId), Callback.ADD_ADMIN.getCallback()),
                                KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_REMOVE_ADMIN, chatId), Callback.REMOVE_ADMIN.getCallback())
                                ))
                .keyboardRow(List.of(KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_BOT_SETTINGS,chatId),Callback.BOT_SETTINGS.getCallback())))
                .keyboardRow(List.of(KeyboardUtils.inlineButtonWithWebApp(langService.getMessage(LangFields.BUTTON_CATALOG, chatId), "https://autoexpo2024.uz")))
                .build();

        return SendMessage.builder()
                .chatId(chatId)
                .text(langService.getMessage(LangFields.ADMIN_PANEL, chatId))
                .replyMarkup(markup)
                .build();

    }
    public EditMessageText botSettings(TelegramUser telegramUser,Integer messageId) {
        return EditMessageText.builder()
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .text(langService.getMessage(LangFields.ADMIN_PANEL, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_CHANGE_CURRENCY,telegramUser.getChatId()),Callback.CHANGE_CURRENCY.getCallback())
                ))
                .build();
    }

    public EditMessageText addProduct(TelegramUser telegramUser, Integer messageId) {
        return EditMessageText.builder()
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .text(langService.getMessage(LangFields.INPUT_PRODUCT_NAME, telegramUser.getChatId()))
                .build();
    }

    public SendMessage writePrice(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.INPUT_PRODUCT_PRICE, telegramUser.getChatId()))
                .build();
    }

    public SendMessage invalidPrice(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.INVALID_PRICE, telegramUser.getChatId()))
                .build();
    }

    public SendMessage writeDescription(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.INPUT_PRODUCT_DESCRIPTION, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_SKIP_DESCRIPTION, telegramUser.getChatId()), Callback.SKIP_DESCRIPTION.getCallback()))
                )
                .build();
    }

    public SendMessage chooseCategory(TelegramUser telegramUser) {
        List<InlineKeyboardButton> buttons = new ArrayList<>(categoryRepository.findAll().stream()
                .map(c -> KeyboardUtils.inlineButton(c.getName(), Callback.CATEGORY.getCallback() + c.getId())).toList());
        buttons.add(KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_NEW_CATEGORY, telegramUser.getChatId()), Callback.NEW_CATEGORY.getCallback()));
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.CHOOSE_CATEGORY, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.categoryMarkup(buttons))
                .build();
    }

    public EditMessageText chooseCategory(TelegramUser telegramUser, Integer messageId) {
        List<InlineKeyboardButton> buttons = new ArrayList<>(categoryRepository.findAll().stream()
                .map(c -> KeyboardUtils.inlineButton(c.getName(), Callback.CATEGORY.getCallback() + c.getId())).toList());
        buttons.add(KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_NEW_CATEGORY, telegramUser.getChatId()), Callback.NEW_CATEGORY.getCallback()));
        return EditMessageText.builder()
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .text(langService.getMessage(LangFields.CHOOSE_CATEGORY, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.categoryMarkup(buttons))
                .build();
    }

    public SendMessage sendImage(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.INPUT_PRODUCT_IMAGE, telegramUser.getChatId()))
                .build();
    }

    public EditMessageText sendImage(TelegramUser telegramUser, Integer messageId) {
        return EditMessageText.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.INPUT_PRODUCT_IMAGE, telegramUser.getChatId()))
                .messageId(messageId)
                .build();
    }

    public SendMessage inputDiscount(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.INPUT_PRODUCT_DISCOUNT, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_SKIP_DISCOUNT, telegramUser.getChatId()), Callback.SKIP_DISCOUNT.getCallback())))
                .build();
    }

    public EditMessageText writeCategory(TelegramUser telegramUser, Integer messageId) {
        return EditMessageText.builder()
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .text(langService.getMessage(LangFields.INPUT_CATEGORY_NAME, telegramUser.getChatId()))
                .build();
    }

    public EditMessageText setAdminMethod(TelegramUser telegramUser, Integer messageId) {
        return EditMessageText.builder()
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .text(langService.getMessage(LangFields.CHOOSE_SET_ADMIN_METHOD, telegramUser.getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_SET_ADMIN_BY_PHONE_NUMBER, telegramUser.getChatId()), Callback.BY_PHONE_NUMBER.getCallback()),
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_SET_ADMIN_BY_USERNAME, telegramUser.getChatId()), Callback.BY_USERNAME.getCallback())
                ))
                .build();
    }

    public EditMessageText writeUsername(TelegramUser telegramUser, Integer messageId) {
        return EditMessageText.builder()
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .text(langService.getMessage(LangFields.INPUT_ADMIN_USERNAME, telegramUser.getChatId()))
                .build();
    }

    public EditMessageText writePhoneNumber(TelegramUser telegramUser, Integer messageId) {
        return EditMessageText.builder()
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .text(langService.getMessage(LangFields.INPUT_PHONE_NUMBER, telegramUser.getChatId()))
                .build();
    }

    public EditMessageText writeCurrency(TelegramUser telegramUser, Integer messageId) {
        String message = langService.getMessage(LangFields.INPUT_CURRENCY, telegramUser.getChatId());
        message = message.replaceAll("\\?", UtilLists.currency);
        return EditMessageText.builder()
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .text(message)
                .build();
    }

    public SendMessage successfully(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text("✅")
                .build();
    }

    public SendMessage invalidConfirmCode(TelegramUser telegramUser) {
        return SendMessage.builder()
                .chatId(telegramUser.getChatId())
                .text(langService.getMessage(LangFields.INVALID_CONFIRM_CODE, telegramUser.getChatId()))
                .build();
    }

    public SendMessage sendOrderConfirmation(Order order) {
        return SendMessage.builder()
                .chatId(order.getUser().getChatId())
                .text(langService.getMessage(LangFields.ORDER_CONFIRMATION, order.getUser().getChatId()))
                .replyMarkup(KeyboardUtils.inlineMarkup(List.of(
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_YES, order.getUser().getChatId()), Callback.ORDER_CONFIRM_YES.getCallback()),
                        KeyboardUtils.inlineButton(langService.getMessage(LangFields.BUTTON_NO, order.getUser().getChatId()), Callback.ORDER_CONFIRM_NO.getCallback())
                )))
                .build();
    }

    public EditMessageText cancelOrder(TelegramUser telegramUser, Integer messageId) {
        return EditMessageText.builder()
                .text(langService.getMessage(LangFields.CANCELED_ORDER, telegramUser.getChatId()))
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .build();
    }

    public EditMessageText confirmOrder(TelegramUser telegramUser, Integer messageId, Order order) {
        order = orderRepository.save(order);
        StringBuilder sb = new StringBuilder("BSD-" + order.getId());
        sb.append(" raqamli buyurtma qabul qilindi!\nSavollaringiz bo'lsa operatorimizga murojaat qilishingiz mumkin : ")
                .append("\n").append("+998993310550");
        return EditMessageText.builder()
                .text(sb.toString())
                .chatId(telegramUser.getChatId())
                .messageId(messageId)
                .build();
    }
}
