package uz.hiparts.hipartsuz.service.telegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import uz.hiparts.hipartsuz.dto.AddressDto;
import uz.hiparts.hipartsuz.dto.ProductCreateUpdateDto;
import uz.hiparts.hipartsuz.dto.ProductDto;
import uz.hiparts.hipartsuz.exception.NotFoundException;
import uz.hiparts.hipartsuz.model.Branch;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.Product;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.Callback;
import uz.hiparts.hipartsuz.model.enums.LangFields;
import uz.hiparts.hipartsuz.model.enums.OrderType;
import uz.hiparts.hipartsuz.model.enums.PaymentType;
import uz.hiparts.hipartsuz.model.enums.Role;
import uz.hiparts.hipartsuz.model.enums.UserState;
import uz.hiparts.hipartsuz.repository.OrderRepository;
import uz.hiparts.hipartsuz.repository.ProductRepository;
import uz.hiparts.hipartsuz.service.BotSettingsService;
import uz.hiparts.hipartsuz.service.BranchService;
import uz.hiparts.hipartsuz.service.CategoryService;
import uz.hiparts.hipartsuz.service.LangService;
import uz.hiparts.hipartsuz.service.ProductService;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.service.UserService;
import uz.hiparts.hipartsuz.service.impl.ExportXLSXFile;
import uz.hiparts.hipartsuz.service.impl.PaymentServiceClick;
import uz.hiparts.hipartsuz.service.impl.PaymentServicePayme;
import uz.hiparts.hipartsuz.util.Regex;
import uz.hiparts.hipartsuz.util.UtilLists;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private final BotService botService;
    private final SMSService smsService;
    private final LangService langService;
    private final UserService userService;
    private final BranchService branchService;
    private final ExportXLSXFile exportXLSXFile;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final SendMessageService sendMessageService;
    private final BotSettingsService botSettingsService;
    private final TelegramUserService telegramUserService;
    private final PaymentServiceClick paymentServiceClick;
    private final PaymentServicePayme paymentServicePayme;


    public void handleMessage(Message message) {

        if (message.hasText()) {

            TelegramUser telegramUser = new TelegramUser();

            String text = message.getText();

            if (text.equals("/start")) {

                if (telegramUserService.getByChatId(message.getChatId()) == null) {

                    telegramUser.setChatId(message.getChatId());
                    telegramUser.setLang("uz");
                    telegramUser.setState(UserState.START);

                    telegramUserService.save(telegramUser);

                    botService.send(sendMessageService.firstStart(telegramUser));

                } else {

                    User user = userService.getByChatId(message.getChatId());

                    telegramUser = telegramUserService.getByChatId(message.getChatId());

                    if (user != null) {

                        if (user.getRole().equals(Role.ADMIN)) {

                            botService.send(sendMessageService.welcomeAdmin(telegramUser));

                            telegramUserService.setState(message.getChatId(), UserState.START);

                            return;
                        }
                    }

                    telegramUserService.setState(message.getChatId(), UserState.INPUT_PHONE_NUMBER);
                    botService.send(sendMessageService.welcomeUser(telegramUser));

                }
            } else if (text.equals(langService.getMessage(LangFields.BUTTON_SETTINGS, message.getChatId()))) {

                telegramUserService.setState(message.getChatId(), UserState.DEFAULT);

                botService.send(sendMessageService.changeLang(message.getChatId()));

            } else if (text.equals(langService.getMessage(LangFields.BUTTON_NEW_ORDER, message.getChatId()))) {

                telegramUser = telegramUserService.getByChatId(message.getChatId());

                telegramUserService.setState(message.getChatId(), UserState.INPUT_PHONE_NUMBER);

                botService.send(sendMessageService.start(telegramUser));

            } else if (text.equals("Admin") && userService.getByChatId(message.getChatId()).getRole().equals(Role.ADMIN)) {

                telegramUserService.setState(message.getChatId(), UserState.DEFAULT);

                telegramUser = telegramUserService.getByChatId(message.getChatId());

                botService.send(sendMessageService.adminPanel(telegramUser));

            }
        }
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {

        String data = callbackQuery.getData();

        TelegramUser telegramUser = telegramUserService.getByChatId(callbackQuery.getMessage().getChatId());

        if (data.startsWith("lang")) {
            botService.send(sendMessageService.deleteMessage(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId()));
            botService.send(sendMessageService.setLang(data, telegramUser));
            return;
        }
        if (data.startsWith(Callback.BRANCH.getCallback())) {

            Long branchId = Long.parseLong(data.split("-")[1]);
            Branch branch = branchService.getById(branchId);
            Order order = UtilLists.orderMap.get(callbackQuery.getMessage().getChatId());

            order.setOrderType(OrderType.PICK_UP);
            order.setLat(branch.getLat());
            order.setLon(branch.getLon());
            order.setAddress(branch.getName());
            order.setBranch(branch.getName());

            UtilLists.orderMap.put(callbackQuery.getMessage().getChatId(), order);

            botService.send(
                    DeleteMessage.builder()
                            .messageId(callbackQuery.getMessage().getMessageId())
                            .chatId(telegramUser.getChatId())
                            .build()
            );

            telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);

            botService.send(sendMessageService.sendCatalog(telegramUser));
            return;
        }
        if (data.startsWith(Callback.BRANCH_DELETE.getCallback())) {

            Long branchId = Long.parseLong(data.split("-")[1]);
            branchService.delete(branchId);

            botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
            botService.send(sendMessageService.successfully(telegramUser));

            telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);

            botService.send(sendMessageService.welcomeAdmin(telegramUser));

            return;
        }
        if (data.startsWith(Callback.CATEGORY.getCallback())) {
            Long categoryId = Long.parseLong(data.split("-")[1]);
            Category category = categoryService.getById(categoryId);

            ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreate.get(callbackQuery.getMessage().getChatId());

            productCreateUpdateDto.setCategory(category);

            productService.create(productCreateUpdateDto);

            UtilLists.productCreate.put(callbackQuery.getMessage().getChatId(), null);

            botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
            botService.send(sendMessageService.successfully(telegramUser));
            botService.send(sendMessageService.adminPanel(telegramUser));

            telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
            return;
        }
        if (data.startsWith(Callback.CATEGORY_DELETE.getCallback())) {
            Long branchId = Long.parseLong(data.split("-")[1]);

            categoryService.delete(branchId);

            botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
            botService.send(sendMessageService.successfully(telegramUser));

            telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);

            botService.send(sendMessageService.welcomeAdmin(telegramUser));
            return;
        }
        if (data.startsWith(Callback.CHANGED_CATEGORY.getCallback())) {
            Long categoryId = Long.parseLong(data.split("-")[1]);
            Category category = categoryService.getById(categoryId);

            ProductDto productCreateUpdateDto = UtilLists.productUpdate.get(callbackQuery.getMessage().getChatId());

            productCreateUpdateDto.setCategory(category);

            UtilLists.productCreate.put(callbackQuery.getMessage().getChatId(), new ProductCreateUpdateDto(productCreateUpdateDto));

            botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
            botService.send(sendMessageService.changeProduct(telegramUser));

            telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
            return;
        }
        Callback callback = Callback.of(data);
        switch (callback) {
            case CHANGE_LANGUAGE ->
                    botService.send(sendMessageService.changeLang(telegramUser, callbackQuery.getMessage().getMessageId()));
            case DELIVERY, CONFIRM_LOCATION_NO -> {

                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_LOCATION);

                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.askDeliveryLocation(telegramUser));
            }
            case PICK_UP -> {
                List<Branch> branches = branchService.getAll();
                botService.send(sendMessageService.sendBranches(branches, callbackQuery.getMessage().getMessageId(), telegramUser));
            }
            case CONFIRM_LOCATION_YES -> {
                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.sendLocation(telegramUser, UtilLists.orderMap.get(callbackQuery.getMessage().getChatId()).getAddress()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                botService.send(sendMessageService.sendCatalog(telegramUser));
            }
            case ADD_PRODUCT -> {
                botService.send(sendMessageService.writeProductName(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_NAME);
            }
            case CHANGE_PRODUCT -> {
                botService.send(sendMessageService.writeProductId(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_ID);
            }
            case BACK_TO_ADMIN_PANEL -> {
                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.adminPanel(telegramUser));
                telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
            }
            case BACK_TO_CHANGE_LANG -> {
                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.changeLang(telegramUser.getChatId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
            }
            case BACK_TO_MAIN_MENU -> {
                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                User user = userService.getByChatId(callbackQuery.getMessage().getChatId());
                if (user != null) {
                    if (user.getRole().equals(Role.ADMIN)) {
                        botService.send(sendMessageService.welcomeAdmin(telegramUser));
                        telegramUserService.setState(callbackQuery.getMessage().getChatId(), UserState.START);
                    } else {
                        botService.send(sendMessageService.welcomeUser(telegramUser));
                        telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    }
                }
            }
            case BACK_TO_CHOOSE_ORDER_TYPE -> {
                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.chooseOrderType(telegramUser));
                telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
            }
            case CHANGE_PRODUCT_NAME -> {
                botService.send(sendMessageService.changeProductName(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_NEW_PRODUCT_NAME);
            }
            case CHANGE_PRODUCT_DESCRIPTION -> {
                botService.send(sendMessageService.changeProductDescription(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_NEW_PRODUCT_DESCRIPTION);
            }
            case CHANGE_PRODUCT_PRICE -> {
                botService.send(sendMessageService.changeProductPrice(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_NEW_PRODUCT_PRICE);
            }
            case CHANGE_PRODUCT_DISCOUNT -> {
                botService.send(sendMessageService.changeProductDiscount(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_NEW_PRODUCT_DISCOUNT);
            }
            case CHANGE_PRODUCT_IMAGE -> {
                ProductDto productDto = UtilLists.productUpdate.get(callbackQuery.getMessage().getChatId());
                botService.send(sendMessageService.deleteMessage(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.sendProductImg(callbackQuery.getMessage().getChatId(), productDto.getImgId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_NEW_PRODUCT_IMAGE);
            }
            case CHANGE_PRODUCT_CATEGORY -> {
                botService.send(sendMessageService.changeProductCategory(telegramUser, callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.changeCategory(telegramUser));
                telegramUserService.setState(telegramUser.getChatId(), UserState.CHOOSE_PRODUCT_CATEGORY);
            }
            case CONFIRM_PRODUCT_CHANGES -> {
                ProductDto productDto = UtilLists.productUpdate.get(callbackQuery.getMessage().getChatId());
                productService.update(new ProductCreateUpdateDto(productDto));
                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.successfully(telegramUser));
                botService.send(sendMessageService.adminPanel(telegramUser));
                telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
            }
            case SKIP_DESCRIPTION -> {
                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.writePrice(telegramUser));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_PRICE);
            }
            case SKIP_DISCOUNT -> {
                ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreate.get(callbackQuery.getMessage().getChatId());
                productCreateUpdateDto.setDiscount(0D);
                UtilLists.productCreate.put(callbackQuery.getMessage().getChatId(), productCreateUpdateDto);
                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.sendImage(telegramUser));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_IMAGE);
            }
            case NEW_CATEGORY -> {
                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.writeCategory(telegramUser));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_CATEGORY);
            }
            case CHANGE_NEW_CATEGORY -> {
                botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), callbackQuery.getMessage().getMessageId()));
                botService.send(sendMessageService.writeCategory(telegramUser));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_NEW_PRODUCT_CATEGORY);
            }
            case CHANGE_CURRENCY -> {
                botService.send(sendMessageService.writeCurrency(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(callbackQuery.getMessage().getChatId(), UserState.INPUT_CURRENCY);
            }
            case CHANGE_DELIVERY_PRICE -> {
                botService.send(sendMessageService.writeDeliveryPrice(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(callbackQuery.getMessage().getChatId(), UserState.INPUT_DELIVERY_PRICE);
            }
            case CHANGE_OPERATOR_NUMBER -> {
                botService.send(sendMessageService.writeOperatorNumber(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(callbackQuery.getMessage().getChatId(), UserState.INPUT_OPERATOR_NUMBER);
            }
            case REMOVE_ADMIN -> {
                botService.send(sendMessageService.removeAdminMethod(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.CHOOSE_METHOD_REMOVE_ADMIN);
            }
            case ADD_ADMIN -> {
                botService.send(sendMessageService.setAdminMethod(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.CHOOSE_METHOD_SET_ADMIN);
            }
            case SET_BY_PHONE_NUMBER -> {
                botService.send(sendMessageService.writePhoneNumber(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_ADMIN_PHONE_NUMBER_FOR_SET);
            }
            case SET_BY_USERNAME -> {
                botService.send(sendMessageService.writeUsername(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_ADMIN_USERNAME_FOR_SET);
            }
            case REMOVE_BY_PHONE_NUMBER -> {
                botService.send(sendMessageService.writePhoneNumber(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_ADMIN_PHONE_NUMBER_FOR_REMOVE);
            }
            case REMOVE_BY_USERNAME -> {
                botService.send(sendMessageService.writeUsername(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_ADMIN_USERNAME_FOR_REMOVE);
            }
            case CONFIRM_ORDER_NO -> {
                botService.send(sendMessageService.cancelOrder(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                UtilLists.orderMap.put(callbackQuery.getMessage().getChatId(), null);
            }
            case CONFIRM_ORDER_YES -> {
                Order order = UtilLists.orderMap.get(callbackQuery.getMessage().getChatId());
                System.out.println(UtilLists.orderMap.get(callbackQuery.getMessage().getChatId()));
                if (order.getPaymentType() != PaymentType.CASH) {
                    if (order.getPaymentType() == PaymentType.CLICK) {
                        String paymentUrl = paymentServiceClick.sendInvoice(order);
                        botService.send(sendMessageService.sendPaymentMessage(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId(),paymentUrl));
                    } else if (order.getPaymentType() == PaymentType.PAYME) {
                        String paymentUrl = paymentServicePayme.sendInvoice(order);
                        botService.send(sendMessageService.sendPaymentMessage(callbackQuery.getMessage().getChatId(),callbackQuery.getMessage().getMessageId(),paymentUrl));
                    }
                } else {
                    orderRepository.save(order);
                    botService.send(sendMessageService.confirmOrder(telegramUser, callbackQuery.getMessage().getMessageId(), UtilLists.orderMap.get(callbackQuery.getMessage().getChatId())));
                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                }
            }
            case ADD_BRANCH -> {
                botService.send(sendMessageService.addBranch(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_BRANCH_NAME);
            }
            case DELETE_BRANCH -> {
                botService.send(sendMessageService.deleteBranch(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.CHOOSE_BRANCH_FOR_DELETE);
            }
            case ADD_CATEGORY -> {
                botService.send(sendMessageService.inputCategoryName(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_CATEGORY_NAME);
            }
            case DELETE_CATEGORY -> {
                botService.send(sendMessageService.deleteCategory(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.CHOOSE_CATEGORY_FOR_DELETE);
            }
            case BOT_SETTINGS -> {
                botService.send(sendMessageService.botSettings(telegramUser, callbackQuery.getMessage().getMessageId()));
                telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
            }
//            case PAYED -> {
//                boolean isPaid = paymentServiceClick.checkInvoice(UtilLists.orderMap.get(callbackQuery.getMessage().getChatId()).getInvoiceId());
//
//                if (isPaid) {
//                    botService.send(sendMessageService.confirmOrder(telegramUser, callbackQuery.getMessage().getMessageId(), UtilLists.orderMap.get(callbackQuery.getMessage().getChatId())));
//                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
//                } else
//                    botService.send(sendMessageService.sendPaymentMessage(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId(),));
//            }
            case DELETE_PRODUCT -> {
                productService.delete(UtilLists.productUpdate.get(telegramUser.getChatId()).getId());
                telegramUserService.setState(callbackQuery.getMessage().getChatId(), UserState.DEFAULT);
                botService.send(sendMessageService.successfully(telegramUser));
                botService.send(sendMessageService.adminPanel(telegramUser));
            }
            case EXPORT_PRODUCTS -> {
                try {
                    botService.sendFile(callbackQuery.getMessage().getChatId(), exportXLSXFile.exportXLSXFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
        if (message.hasText()) {

            String text = message.getText();

            if (user != null) {
                if ((text.equals(langService.getMessage(LangFields.BUTTON_BACK, message.getChatId())) && (telegramUser.getState() == UserState.INPUT_PHONE_NUMBER))) {
                    if (user.getRole().equals(Role.ADMIN)) {
                        botService.send(sendMessageService.welcomeAdmin(telegramUser));
                        telegramUserService.setState(message.getChatId(), UserState.START);
                    } else {
                        botService.send(sendMessageService.welcomeUser(telegramUser));
                        telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    }
                } else if ((text.equals(langService.getMessage(LangFields.BUTTON_BACK, message.getChatId())) && (telegramUser.getState() == UserState.INPUT_CONFIRM_CODE))) {
                    if (user.getRole().equals(Role.ADMIN)) {
                        botService.send(sendMessageService.welcomeAdmin(telegramUser));
                        telegramUserService.setState(message.getChatId(), UserState.START);
                    } else {
                        botService.send(sendMessageService.welcomeUser(telegramUser));
                        telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    }
                } else if (text.equals(langService.getMessage(LangFields.BUTTON_BACK, message.getChatId())) && telegramUser.getState() == UserState.INPUT_LOCATION) {
                    botService.send(sendMessageService.chooseOrderType(telegramUser));
                    telegramUserService.setState(message.getChatId(), UserState.CHOOSE_ORDER_TYPE);
                }
            }
        }

        switch (telegramUser.getState()) {
            case INPUT_PHONE_NUMBER -> {

                String phoneNumber;

                if (message.hasContact()) {

                    phoneNumber = message.getContact().getPhoneNumber();

                    if (!phoneNumber.startsWith("+")) phoneNumber = "+" + phoneNumber;

                } else phoneNumber = "+998" + message.getText();

                if (!phoneNumber.matches(Regex.PHONE_NUMBER)) {
                    telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PHONE_NUMBER);
                    botService.send(sendMessageService.start(telegramUser));
                    return;
                }
                if (!Objects.equals(user == null ? null : user.getLastPhoneNumber(), phoneNumber)) {
                    smsService.send(telegramUser, phoneNumber);
                    return;
                }
                smsService.savePhoneNumber(telegramUser, phoneNumber);
            }
            case INPUT_CONFIRM_CODE -> {

                if (message.hasText()) {

                    String code = message.getText();

                    if (code.equals(langService.getMessage(LangFields.BUTTON_CHANGE_PHONE_NUMBER, message.getChatId()))) {
                        botService.send(sendMessageService.start(telegramUser));
                        telegramUserService.setState(message.getChatId(), UserState.INPUT_PHONE_NUMBER);
                    } else if (code.equals(langService.getMessage(LangFields.BUTTON_RESEND_CODE, message.getChatId()))) {
                        smsService.send(telegramUser);
                    } else {
                        try {
                            if (smsService.check(telegramUser, Integer.parseInt(code)))
                                smsService.savePhoneNumber(telegramUser);
                            else botService.send(sendMessageService.invalidConfirmCode(telegramUser));
                        } catch (NumberFormatException e) {
                            botService.send(sendMessageService.invalidConfirmCode(telegramUser));
                        }
                    }
                } else botService.send(sendMessageService.invalidConfirmCode(telegramUser));
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
                        botService.send(sendMessageService.sendAddressDetails(addressDetails, telegramUser));
                    } else botService.send(sendMessageService.invalidShippingAddress(telegramUser));
                } else botService.send(sendMessageService.invalidShippingAddress(telegramUser));
            }

            case INPUT_PRODUCT_NAME -> {
                if (message.hasText()) {
                    String productName = message.getText();
                    UtilLists.productCreate.put(message.getChatId(), ProductCreateUpdateDto.builder().name(productName).build());
                    botService.send(sendMessageService.writeDescription(telegramUser));
                    telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_DESCRIPTION);
                }
            }

            case INPUT_PRODUCT_DESCRIPTION -> {
                if (message.hasText()) {
                    String productDescription = message.getText();
                    ProductCreateUpdateDto productDto = UtilLists.productCreate.get(message.getChatId());
                    productDto.setDescription(productDescription);
                    UtilLists.productCreate.put(message.getChatId(), productDto);
                    botService.send(sendMessageService.writePrice(telegramUser));
                    telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_PRICE);
                }
            }

            case INPUT_PRODUCT_PRICE -> {
                if (message.hasText()) {
                    String productPrice = message.getText();
                    ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreate.get(message.getChatId());
                    if (isNaturalNumber(productPrice) && productPrice.matches(Regex.PRICE)) {
                        double price = Double.parseDouble(productPrice);
                        double currency = Double.parseDouble(botSettingsService.getCurrency());
                        productCreateUpdateDto.setPrice(price * currency);
                        UtilLists.productCreate.put(message.getChatId(), productCreateUpdateDto);
                        telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_DISCOUNT);

                        botService.send(sendMessageService.inputDiscount(telegramUser));
                    } else
                        botService.send(sendMessageService.invalidPrice(telegramUser));
                }
            }

            case INPUT_PRODUCT_DISCOUNT -> {
                if (message.hasText()) {
                    String discount = message.getText();
                    if (isNaturalNumber(discount) && discount.matches(Regex.PRICE)) {
                        ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreate.get(message.getChatId());
                        productCreateUpdateDto.setDiscount(Double.valueOf(discount));
                        UtilLists.productCreate.put(message.getChatId(), productCreateUpdateDto);
                        botService.send(sendMessageService.sendImage(telegramUser));
                        telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_PRODUCT_IMAGE);
                    } else {
                        botService.send(sendMessageService.invalidDiscount(telegramUser));
                    }
                }
            }

            case INPUT_PRODUCT_IMAGE -> {
                if (message.hasPhoto()) {
                    String fileId = message.getPhoto().stream()
                            .max(Comparator.comparingInt(PhotoSize::getFileSize))
                            .map(PhotoSize::getFileId)
                            .orElseThrow(() -> new IllegalArgumentException("No photo found"));
                    ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreate.get(message.getChatId());
                    productCreateUpdateDto.setImgPath(botService.getFile(fileId));
                    productCreateUpdateDto.setImgId(fileId);
                    UtilLists.productCreate.put(message.getChatId(), productCreateUpdateDto);
                    botService.send(sendMessageService.chooseCategory(telegramUser));
                    telegramUserService.setState(telegramUser.getChatId(), UserState.CHOOSE_PRODUCT_CATEGORY);
                }
            }

            case INPUT_PRODUCT_CATEGORY -> {
                if (message.hasText()) {
                    String categoryName = message.getText();
                    try {
                        Category existingCategory = categoryService.getByName(categoryName);
                        if (existingCategory != null) {
                            botService.send(sendMessageService.duplicateCategoryError(telegramUser));
                            botService.send(sendMessageService.adminPanel(telegramUser));
                        }
                    } catch (NotFoundException e) {
                        ProductCreateUpdateDto productCreateUpdateDto = UtilLists.productCreate.get(message.getChatId());
                        Category newCategory = categoryService.save(categoryName);
                        productCreateUpdateDto.setCategory(newCategory);
                        productService.create(productCreateUpdateDto);
                        UtilLists.productCreate.put(message.getChatId(), null);
                        botService.send(sendMessageService.deleteMessage(telegramUser.getChatId(), message.getMessageId()));
                        botService.send(sendMessageService.successfully(telegramUser));
                        botService.send(sendMessageService.adminPanel(telegramUser));
                        telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    }
                }
            }

            case INPUT_PRODUCT_ID -> {
                if (message.hasText()) {
                    String productId = message.getText();
                    try {
                        ProductDto productDto = productService.getById(Long.valueOf(productId));
                        botService.send(sendMessageService.changeProduct(telegramUser));
                        telegramUserService.setState(telegramUser.getChatId(), UserState.CHANGE_PRODUCT);
                        UtilLists.productUpdate.put(message.getChatId(), productDto);
                    } catch (NotFoundException e) {
                        botService.send(sendMessageService.invalidProductId(telegramUser));
                    } catch (NumberFormatException e) {
                        botService.send(sendMessageService.invalidNumberFormat(telegramUser));
                    }
                }
            }

            case INPUT_NEW_PRODUCT_NAME -> {
                if (message.hasText()) {
                    String productName = message.getText();
                    ProductDto productDto = UtilLists.productUpdate.get(message.getChatId());
                    productDto.setName(productName);
                    UtilLists.productUpdate.put(message.getChatId(), productDto);
                    botService.send(sendMessageService.changeProduct(telegramUser));
                }
            }

            case INPUT_NEW_PRODUCT_DESCRIPTION -> {
                if (message.hasText()) {
                    String productDescription = message.getText();
                    ProductDto productDto = UtilLists.productUpdate.get(message.getChatId());
                    productDto.setDescription(productDescription);
                    UtilLists.productUpdate.put(message.getChatId(), productDto);
                    botService.send(sendMessageService.changeProduct(telegramUser));
                }
            }

            case INPUT_NEW_PRODUCT_PRICE -> {
                if (message.hasText()) {
                    String productPrice = message.getText();
                    if (isNaturalNumber(productPrice) && productPrice.matches(Regex.PRICE)) {
                        ProductDto productDto = UtilLists.productUpdate.get(message.getChatId());
                        productDto.setPrice(Double.parseDouble(productPrice));
                        UtilLists.productUpdate.put(message.getChatId(), productDto);
                        botService.send(sendMessageService.changeProduct(telegramUser));
                    } else {
                        botService.send(sendMessageService.invalidPrice(telegramUser));
                    }
                }
            }

            case INPUT_NEW_PRODUCT_DISCOUNT -> {
                if (message.hasText()) {
                    String discount = message.getText();
                    if (isNaturalNumber(discount)) {
                        ProductDto productDto = UtilLists.productUpdate.get(message.getChatId());
                        productDto.setDiscount(Double.parseDouble(discount));
                        UtilLists.productUpdate.put(message.getChatId(), productDto);
                        botService.send(sendMessageService.changeProduct(telegramUser));
                    } else {
                        botService.send(sendMessageService.invalidDiscount(telegramUser));
                    }
                }
            }

            case INPUT_NEW_PRODUCT_IMAGE -> {
                if (message.hasPhoto()) {
                    String fileId = message.getPhoto().stream()
                            .max(Comparator.comparingInt(PhotoSize::getFileSize))
                            .map(PhotoSize::getFileId)
                            .orElseThrow(() -> new IllegalArgumentException("No photo found"));
                    ProductDto productDto = UtilLists.productUpdate.get(message.getChatId());
                    productDto.setImgPath(botService.getFile(fileId));
                    productDto.setImgId(fileId);
                    UtilLists.productUpdate.put(message.getChatId(), productDto);
                    botService.send(sendMessageService.successfully(telegramUser));
                    botService.send(sendMessageService.changeProduct(telegramUser));
                }
            }

            case INPUT_NEW_PRODUCT_CATEGORY -> {
                if (message.hasText()) {
                    String categoryName = message.getText();
                    try {
                        Category existingCategory = categoryService.getByName(categoryName);
                        if (existingCategory != null) {
                            botService.send(sendMessageService.duplicateCategoryError(telegramUser));
                            botService.send(sendMessageService.changeProduct(telegramUser));
                        }
                    } catch (NotFoundException e) {
                        ProductDto productDto = UtilLists.productUpdate.get(message.getChatId());
                        Category newCategory = categoryService.save(categoryName);
                        productDto.setCategory(newCategory);
                        UtilLists.productUpdate.put(message.getChatId(), productDto);
                        productService.update(new ProductCreateUpdateDto(productDto));
                        botService.send(sendMessageService.changeProduct(telegramUser));
                    }
                }
            }

            case INPUT_ADMIN_USERNAME_FOR_SET -> {
                if (message.hasText()) {
                    String username = message.getText();
                    userService.setAdminByUsername(username);
                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    botService.send(sendMessageService.successfully(telegramUser));
                    botService.send(sendMessageService.welcomeAdmin(telegramUser));
                }
            }

            case INPUT_ADMIN_PHONE_NUMBER_FOR_SET -> {
                if (message.hasText()) {
                    String phoneNumber = message.getText();
                    userService.setAdminByPhoneNumber(phoneNumber);
                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    botService.send(sendMessageService.successfully(telegramUser));
                    botService.send(sendMessageService.welcomeAdmin(telegramUser));
                }
            }

            case INPUT_ADMIN_USERNAME_FOR_REMOVE -> {
                if (message.hasText()) {
                    String username = message.getText();
                    userService.removeAdminByUsername(username);
                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    botService.send(sendMessageService.successfully(telegramUser));
                    botService.send(sendMessageService.welcomeAdmin(telegramUser));
                }
            }

            case INPUT_ADMIN_PHONE_NUMBER_FOR_REMOVE -> {
                if (message.hasText()) {
                    String phoneNumber = message.getText();
                    userService.removeAdminByPhoneNumber(phoneNumber);
                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    botService.send(sendMessageService.successfully(telegramUser));
                    botService.send(sendMessageService.welcomeAdmin(telegramUser));
                }
            }

            case INPUT_CURRENCY -> {
                if (message.hasText()) {
                    String rate = message.getText();
                    if (rate.matches(Regex.PRICE)) {
                        try {
                            String existingRate = botSettingsService.getCurrency();
                            List<Product> all = productRepository.findAll();
                            all.forEach(
                                    p -> p.setPrice((p.getPrice() / Double.parseDouble(existingRate)) * Double.parseDouble(rate))
                            );
                            productRepository.saveAll(all);
                            botSettingsService.setCurrency(rate);
                            botService.send(sendMessageService.successfully(telegramUser));
                            botService.send(sendMessageService.adminPanel(telegramUser));
                        } catch (NumberFormatException ignore) {
                        }
                    } else
                        botService.send(sendMessageService.invalidPrice(telegramUser));
                }
            }
            case INPUT_DELIVERY_PRICE -> {
                if (message.hasText()) {
                    String deliveryPrice = message.getText();
                    if (deliveryPrice.matches(Regex.PRICE)) {
                        botSettingsService.setDeliveryPrice(deliveryPrice);
                        botService.send(sendMessageService.successfully(telegramUser));
                        botService.send(sendMessageService.adminPanel(telegramUser));
                    } else
                        botService.send(sendMessageService.invalidPrice(telegramUser));
                }
            }
            case INPUT_OPERATOR_NUMBER -> {
                if (message.hasText()) {
                    String operatorNumber = message.getText();
                    if (operatorNumber.matches(Regex.PHONE_NUMBER)) {
                        botSettingsService.setOperatorNumber(operatorNumber);
                        botService.send(sendMessageService.successfully(telegramUser));
                        botService.send(sendMessageService.adminPanel(telegramUser));
                    } else
                        botService.send(sendMessageService.invalidNumberFormat(telegramUser));
                }
            }

            case INPUT_BRANCH_NAME -> {
                if (message.hasText()) {
                    String branchName = message.getText();
                    UtilLists.branchCreate.put(message.getChatId(), Branch.builder().name(branchName).build());
                    telegramUserService.setState(telegramUser.getChatId(), UserState.INPUT_BRANCH_LOCATION);
                    botService.send(sendMessageService.askBranchLocation(telegramUser));
                }
            }

            case INPUT_BRANCH_LOCATION -> {
                if (message.hasLocation()) {
                    Location location = message.getLocation();
                    AddressDto addressDetails = new RestTemplate().getForObject("https://nominatim.openstreetmap.org/reverse?format=json&lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&zoom=155&addressdetails=1", AddressDto.class);
                    if (addressDetails != null) {
                        Branch branch = UtilLists.branchCreate.get(message.getChatId());
                        branch.setLat(location.getLatitude());
                        branch.setLon(location.getLongitude());
                        branch.setAddress(addressDetails.getDisplayName());
                        branchService.create(branch);
                        UtilLists.branchCreate.put(message.getChatId(), null);
                        botService.send(sendMessageService.successfully(telegramUser));
                        telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                        botService.send(sendMessageService.adminPanel(telegramUser));
                        botService.send(sendMessageService.sendLocation(telegramUser, branch.getAddress()));
                    }
                } else botService.send(sendMessageService.invalidBranchAddress(telegramUser));
            }

            case INPUT_CATEGORY_NAME -> {
                if (message.hasText()) {
                    String categoryName = message.getText();
                    categoryService.save(categoryName);
                    botService.send(sendMessageService.successfully(telegramUser));
                    telegramUserService.setState(telegramUser.getChatId(), UserState.DEFAULT);
                    botService.send(sendMessageService.adminPanel(telegramUser));
                }
            }
        }
    }

    public boolean isOverrideCommand(Message message) {
        return message.getText().equals("/start") || message.getText().equals(langService.getMessage(LangFields.BUTTON_SETTINGS, message.getChatId()))
                || message.getText().equals(langService.getMessage(LangFields.BUTTON_NEW_ORDER, message.getChatId()))
                || message.getText().equals("Admin");
    }

    private boolean isNaturalNumber(String str) {
        try {
            int num = Integer.parseInt(str);
            return num >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
