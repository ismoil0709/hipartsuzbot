package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.hiparts.hipartsuz.dto.AddressDto;
import uz.hiparts.hipartsuz.dto.ProductCreateUpdateDto;
import uz.hiparts.hipartsuz.model.Branch;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.Callback;
import uz.hiparts.hipartsuz.model.enums.LangFields;
import uz.hiparts.hipartsuz.model.enums.OrderType;
import uz.hiparts.hipartsuz.model.enums.Role;
import uz.hiparts.hipartsuz.model.enums.UserState;
import uz.hiparts.hipartsuz.service.BranchService;
import uz.hiparts.hipartsuz.service.CategoryService;
import uz.hiparts.hipartsuz.service.LangService;
import uz.hiparts.hipartsuz.service.ProductService;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.service.UserService;
import uz.hiparts.hipartsuz.util.BotUtils;
import uz.hiparts.hipartsuz.util.Regex;
import uz.hiparts.hipartsuz.util.UtilLists;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TelegramService {
    private final TelegramUserService telegramUserService;
    private final SendMessageService sendMessageService;
    private final LangService langService;
    private final UserService userService;
    private final BranchService branchService;
    private final CategoryService categoryService;
    private final ProductService productService;
//    private final SMSService smsService;

    public void handleMessage(Message message) {
        if (message.hasText()) {
            TelegramUser telegramUser = new TelegramUser();
            String text = message.getText();
            if (text.equals("/start"))
                if (telegramUserService.getByChatId(message.getChatId()) == null) {
                    telegramUser.setChatId(message.getChatId());
                    telegramUser.setLang("uz");
                    telegramUser.setState(UserState.START);
                    telegramUserService.save(telegramUser);
                    BotUtils.send(sendMessageService.firstStart(telegramUser));
                } else {
                    User user = userService.getByChatId(message.getChatId());
                    telegramUser = telegramUserService.getByChatId(message.getChatId());
                    if (user != null) {
                        if (user.getRole().equals(Role.ADMIN)) {
                            BotUtils.send(sendMessageService.welcomeAdmin(telegramUser));
                            telegramUserService.setState(message.getChatId(), UserState.START);
                            return;
                        }
                    }
                    telegramUserService.setState(message.getChatId(), UserState.INPUT_PHONE_NUMBER);
                    BotUtils.send(sendMessageService.start(telegramUser));

                }
            else if (text.equals(langService.getMessage(LangFields.BUTTON_SETTINGS, message.getChatId()))) {
                BotUtils.send(sendMessageService.changeLang(message.getChatId()));
            } else if (text.equals(langService.getMessage(LangFields.BUTTON_NEW_ORDER, message.getChatId()))) {
                telegramUser = telegramUserService.getByChatId(message.getChatId());
                telegramUserService.setState(message.getChatId(), UserState.INPUT_PHONE_NUMBER);
                BotUtils.send(sendMessageService.start(telegramUser));
            } else if (text.equals("Admin") && userService.getByChatId(message.getChatId()).getRole().equals(Role.ADMIN)) {
                telegramUser = telegramUserService.getByChatId(message.getChatId());
                BotUtils.send(sendMessageService.adminPanel(telegramUser));
            }
        }
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        TelegramUser telegramUser = telegramUserService.getByChatId(callbackQuery.getMessage().getChatId());
        if (data.startsWith("lang")) {
            sendMessageService.setLang(data, callbackQuery.getMessage().getMessageId(), telegramUser);
        }
        if (data.startsWith(Callback.BRANCH.getCallback())) {
            Long branchId = Long.parseLong(data.split("-")[1]);
            Branch branch = branchService.getById(branchId);
            Order order = UtilLists.orderMap.get(callbackQuery.getMessage().getChatId());
            order.setOrderType(OrderType.PICK_UP);
            order.setLat(branch.getLat());
            order.setLon(branch.getLon());
            order.setAddress(branch.getName());
            UtilLists.orderMap.put(callbackQuery.getMessage().getChatId(), order);
            BotUtils.send(
                    DeleteMessage.builder()
                            .messageId(callbackQuery.getMessage().getMessageId())
                            .chatId(telegramUser.getChatId())
                            .build()
            );
            telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
            BotUtils.send(sendMessageService.sendCatalog(telegramUser));
            return;
        }
        if(data.startsWith(Callback.CATEGORY.getCallback())) {
            Long categoryId = Long.parseLong(data.split("-")[1]);
            Category category = categoryService.getById(categoryId);
            ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreateUpdateDtoMap.get(callbackQuery.getMessage().getChatId());
            productCreateUpdateDto.setCategory(category);
            UtilLists.productCreateUpdateDtoMap.put(callbackQuery.getMessage().getChatId(), productCreateUpdateDto);
            BotUtils.send(sendMessageService.sendImage(telegramUser,callbackQuery.getMessage().getMessageId()));
            telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_IMAGE);
            return;
        }
        Callback callback = Callback.of(data);
        switch (callback) {
            case CHANGE_LANGUAGE ->
                    BotUtils.send(sendMessageService.changeLang(telegramUser, callbackQuery.getMessage().getMessageId()));
            case DELIVERY, LOCATION_CONFIRM_NO -> {
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_LOCATION);
                BotUtils.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                BotUtils.send(sendMessageService.askDeliveryLocation(telegramUser));
            }
            case PICK_UP -> {
                List<Branch> branches = branchService.getAll();
                BotUtils.send(sendMessageService.sendBranches(branches, callbackQuery.getMessage().getMessageId(), telegramUser));
            }
            case LOCATION_CONFIRM_YES -> {
                BotUtils.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                BotUtils.send(sendMessageService.sendLocation(telegramUser, UtilLists.orderMap.get(callbackQuery.getMessage().getChatId()).getAddress()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                BotUtils.send(sendMessageService.sendCatalog(telegramUser));
            }
            case ADD_PRODUCT -> {
                BotUtils.send(sendMessageService.addProduct(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_NAME);
            }
            case SKIP_DESCRIPTION -> {
                BotUtils.send(sendMessageService.chooseCategory(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
            }
            case SKIP_DISCOUNT -> {
                ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreateUpdateDtoMap.get(callbackQuery.getMessage().getChatId());
                productCreateUpdateDto.setDiscount(0D);
                productService.create(productCreateUpdateDto);
                UtilLists.productCreateUpdateDtoMap.put(callbackQuery.getMessage().getChatId(),null);
                telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                BotUtils.send(sendMessageService.deleteMessage(callbackQuery.getMessage().getChatId(),callbackQuery.getMessage().getMessageId()));
                BotUtils.send(sendMessageService.adminPanel(telegramUser));
            }
            case NEW_CATEGORY -> {
                BotUtils.send(sendMessageService.writeCategory(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_CATEGORY_NAME);
            }
            case REMOVE_PRODUCT -> {

            }
            case ADD_ADMIN -> {
                BotUtils.send(sendMessageService.setAdminMethod(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.CHOOSE_METHOD_SET_ADMIN);
            }
            case BY_PHONE_NUMBER -> {
                BotUtils.send(sendMessageService.writePhoneNumber(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_ADMIN_PHONE_NUMBER);
            }
            case BY_USERNAME -> {
                BotUtils.send(sendMessageService.writeUsername(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_ADMIN_USERNAME);
            }
            case ALL_USERS -> {
            }
        }
    }

    public void handleInput(Message message) {
        TelegramUser telegramUser = telegramUserService.getByChatId(message.getChatId());
        User user = userService.getByChatId(message.getChatId());
        if (user == null) {
            user = User.builder()
                    .chatId(telegramUser.getChatId())
                    .name(message.getFrom().getFirstName())
                    .username(message.getFrom().getUserName())
                    .lastPhoneNumber("")
                    .role(Role.USER)
                    .build();
            userService.save(user);
        }
        switch (telegramUser.getState()) {
            case INPUT_PHONE_NUMBER -> {
                String phoneNumber;
                if (message.hasContact()) {
                    phoneNumber = message.getContact().getPhoneNumber();
                    if (!phoneNumber.startsWith("+")) {
                        phoneNumber = "+" + phoneNumber;
                    }
                } else {
                    phoneNumber = "+998" + message.getText();
                }
                if (!phoneNumber.matches(Regex.PHONE_NUMBER)) {
                    telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PHONE_NUMBER);
                    BotUtils.send(sendMessageService.start(telegramUser));
                    return;
                }
                if (!Objects.equals(user == null ? null : user.getLastPhoneNumber(), phoneNumber)) {
                    sendCodeToPhoneNumber(telegramUser, phoneNumber);
                    return;
                }
                UtilLists.userPhoneNumberMap.put(telegramUser.getChatId(), phoneNumber);
                savePhoneNumber(telegramUser, phoneNumber);
            }
            case INPUT_LOCATION -> {
                if (message.hasLocation()) {
                    Location location = message.getLocation();
                    AddressDto addressDetails = new RestTemplate().getForObject("https://nominatim.openstreetmap.org/reverse?format=json&lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&zoom=155&addressdetails=1", AddressDto.class);
                    if (addressDetails != null) {
                        Order order = UtilLists.orderMap.get(message.getChatId());
                        order.setAddress(addressDetails.getDisplayName());
                        order.setLat(location.getLatitude());
                        order.setLon(location.getLongitude());
                        order.setOrderType(OrderType.DELIVERY);
                        UtilLists.orderMap.put(message.getChatId(), order);
                        BotUtils.send(sendMessageService.sendAddressDetails(addressDetails, telegramUser));
                    } else BotUtils.send(sendMessageService.invalidShippingAddress(telegramUser));
                } else BotUtils.send(sendMessageService.invalidShippingAddress(telegramUser));
            }
            case INPUT_CONFIRM_CODE -> {
                if (message.hasText()) {
                    String code = message.getText();
                    if (code.equals(langService.getMessage(LangFields.BUTTON_CHANGE_PHONE_NUMBER, message.getChatId()))) {
                        telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PHONE_NUMBER);
                        BotUtils.send(sendMessageService.start(telegramUser));
                        return;
                    } else if (code.equals(langService.getMessage(LangFields.BUTTON_RESEND_CODE, message.getChatId()))) {
                        sendCodeToPhoneNumber(telegramUser, UtilLists.userPhoneNumberMap.get(telegramUser.getChatId()));
                        return;
                    }
                    Map<String, String> confirmCodeMap = UtilLists.confirmCodeMap.get(message.getChatId());
                    if (confirmCodeMap.get(code) != null) {
                        String number = confirmCodeMap.get(code);
                        savePhoneNumber(telegramUser, number);
                    } else System.out.println("Invalid");
                }
            }
            case INPUT_PRODUCT_NAME -> {
                if (message.hasText()) {
                    String productName = message.getText();
                    UtilLists.productCreateUpdateDtoMap.put(message.getChatId(), ProductCreateUpdateDto.builder().name(productName).build());
                    BotUtils.send(sendMessageService.writePrice(telegramUser));
                    telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_PRICE);

                }
            }
            case INPUT_PRODUCT_PRICE -> {
                if (message.hasText()) {
                    String productPrice = message.getText();
                    ProductCreateUpdateDto productDto = UtilLists.productCreateUpdateDtoMap.get(message.getChatId());
                    if (productPrice.matches(Regex.PRICE)) {
                        productDto.setPrice(Double.valueOf(productPrice));
                        UtilLists.productCreateUpdateDtoMap.put(message.getChatId(), productDto);
                        telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_DESCRIPTION);
                        BotUtils.send(sendMessageService.writeDescription(telegramUser));
                    } else
                        BotUtils.send(sendMessageService.invalidPrice(telegramUser));
                }
            }
            case INPUT_PRODUCT_DESCRIPTION -> {
                if (message.hasText()) {
                    String productDescription = message.getText();
                    ProductCreateUpdateDto productDto = UtilLists.productCreateUpdateDtoMap.get(message.getChatId());
                    productDto.setDescription(productDescription);
                    UtilLists.productCreateUpdateDtoMap.put(message.getChatId(), productDto);
                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    BotUtils.send(sendMessageService.chooseCategory(telegramUser));
                }
            }
            case INPUT_PRODUCT_IMAGE -> {
                if (message.hasPhoto()) {
                    ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreateUpdateDtoMap.get(message.getChatId());
                    productCreateUpdateDto.setImgPath(BotUtils.getFile(message.getPhoto()));
                    UtilLists.productCreateUpdateDtoMap.put(message.getChatId(), productCreateUpdateDto);
                    telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_DISCOUNT);
                    BotUtils.send(sendMessageService.inputDiscount(telegramUser));
                }else {
                    BotUtils.send(sendMessageService.sendImage(telegramUser));
                }
            }
            case INPUT_PRODUCT_DISCOUNT -> {
                if (message.hasText()) {
                    String discount = message.getText();
                    ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreateUpdateDtoMap.get(message.getChatId());
                    productCreateUpdateDto.setDiscount(Double.valueOf(discount));
                    productService.create(productCreateUpdateDto);
                    UtilLists.productCreateUpdateDtoMap.put(message.getChatId(),null);
                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    BotUtils.send(sendMessageService.adminPanel(telegramUser));
                }
            }
            case INPUT_CATEGORY_NAME -> {
                if (message.hasText()) {
                    String categoryName = message.getText();
                    ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreateUpdateDtoMap.get(message.getChatId());
                    productCreateUpdateDto.setCategory(categoryService.save(categoryName));
                    UtilLists.productCreateUpdateDtoMap.put(message.getChatId(),productCreateUpdateDto);
                    telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_IMAGE);
                    BotUtils.send(sendMessageService.sendImage(telegramUser));
                }
            }
            case INPUT_ADMIN_USERNAME -> {
                if (message.hasText()){
                    String username = message.getText();
                    userService.setAdminByUsername(username);
                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    BotUtils.send(sendMessageService.welcomeAdmin(telegramUser));
                }
            }
            case INPUT_ADMIN_PHONE_NUMBER -> {
                if (message.hasText()){
                    String phoneNumber = message.getText();
                    userService.setAdminByPhoneNumber(phoneNumber);
                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    BotUtils.send(sendMessageService.welcomeAdmin(telegramUser));
                }
            }
        }
    }

    public void sendCodeToPhoneNumber(TelegramUser telegramUser, String phoneNumber) {
        telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_CONFIRM_CODE);
        BotUtils.send(sendMessageService.askConfirmCode(telegramUser));
        UtilLists.confirmCodeMap.put(telegramUser.getChatId(),
                Map.of(String.valueOf(new Random().nextInt(9000) + 1000), phoneNumber));
        System.out.println(UtilLists.confirmCodeMap.get(telegramUser.getChatId()));
//                    System.out.println(smsService.sendSMS(phoneNumber));
    }

    public void savePhoneNumber(TelegramUser telegramUser, String phoneNumber) {
        User user = userService.getByChatId(telegramUser.getChatId());
        user.setLastPhoneNumber(phoneNumber);
        telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
        userService.save(user);
        UtilLists.orderMap.put(telegramUser.getChatId(), Order.builder().phoneNumber(phoneNumber).build());
        BotUtils.send(sendMessageService.sendPhoneNumber(phoneNumber, telegramUser));
        BotUtils.send(sendMessageService.chooseOrderType(telegramUser));
        UtilLists.confirmCodeMap.put(telegramUser.getChatId(), null);
    }
}
