package uz.hiparts.hipartsuz.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.hiparts.hipartsuz.dto.ClickDto;
import uz.hiparts.hipartsuz.dto.json.PaycomRequestForm;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.OrderTransaction;
import uz.hiparts.hipartsuz.model.TelegramUser;
import uz.hiparts.hipartsuz.repository.OrderRepository;
import uz.hiparts.hipartsuz.repository.OrderTransactionRepository;
import uz.hiparts.hipartsuz.service.TelegramUserService;
import uz.hiparts.hipartsuz.service.telegramService.BotService;
import uz.hiparts.hipartsuz.service.telegramService.SendMessageService;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class PaymentAspect {

    private final BotService botService;
    private final OrderRepository orderRepository;
    private final SendMessageService sendMessageService;
    private final TelegramUserService telegramUserService;
    private final OrderTransactionRepository orderTransactionRepository;

    @After(value = "execution(* uz.hiparts.hipartsuz.service.impl.PaymentServiceClick.complete(..)) && args(dto)")
    public void checkOrderPaymentClick(ClickDto dto) {

        String orderId = dto.getMerchantTransId();

        Optional<Order> optionalOrder = orderRepository.findById(Long.parseLong(orderId));

        if (optionalOrder.isPresent()) {

            Order order = optionalOrder.get();

            if (!order.isCancelled() || order.isPaid()) {

                TelegramUser telegramUser = telegramUserService.getByChatId(order.getUser().getChatId());

                SendMessage sendMessage = sendMessageService.confirmOrder(telegramUser, order);

                botService.send(sendMessage);

            }

        }


    }

    @After(value = "execution(* uz.hiparts.hipartsuz.service.impl.PaymentServicePayme.payWithPaycom(..)) && args(requestForm,authorization)", argNames = "requestForm,authorization")
    public void checkOrderPaymentPayme(PaycomRequestForm requestForm,String authorization) {

        if (requestForm == null || requestForm.getParams() == null || requestForm.getParams().getId() == null) {
            return;
        }

        String transactionId = requestForm.getParams().getId();

        Optional<OrderTransaction> optionalOrderTransaction = orderTransactionRepository.findByTransactionId(transactionId);

        if (optionalOrderTransaction.isPresent()) {

            OrderTransaction orderTransaction = optionalOrderTransaction.get();

            Long orderId = orderTransaction.getOrderId();

            Optional<Order> optionalOrder = orderRepository.findById(orderId);

            if (optionalOrder.isPresent()) {

                Order order = optionalOrder.get();

                if (!order.isCancelled() || order.isPaid()) {

                    TelegramUser telegramUser = telegramUserService.getByChatId(order.getUser().getChatId());

                    SendMessage sendMessage = sendMessageService.confirmOrder(telegramUser, order);

                    botService.send(sendMessage);

                }

            }

        }

    }
}
