package uz.hiparts.hipartsuz.service.impl;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.json.Account;
import uz.hiparts.hipartsuz.dto.json.AdditionalInfo;
import uz.hiparts.hipartsuz.dto.json.CheckPerformTransactionAllowResponse;
import uz.hiparts.hipartsuz.dto.json.PaycomRequestForm;
import uz.hiparts.hipartsuz.dto.json.ResultForm;
import uz.hiparts.hipartsuz.dto.json.Transaction;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.OrderTransaction;
import uz.hiparts.hipartsuz.model.enums.TransactionState;
import uz.hiparts.hipartsuz.repository.OrderRepository;
import uz.hiparts.hipartsuz.repository.OrderTransactionRepository;

import java.nio.charset.Charset;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServicePayme {

    @Value("${secret.payme.secret_key_test}")
    private String secretKeyTest;
    @Value("${secret.payme.secret_key_prod}")
    private String secretKeyProd;
    @Value("${secret.payme.merchant_id}")
    private String merchantId;

    private final OrderRepository orderRepository;
    private final OrderTransactionRepository orderTransactionRepository;

    private final Long TIME_EXPIRED_PAYCOM_ORDER = 43_200_000L;

    public String sendInvoice(Order order){

        order = orderRepository.save(order);

        String url = "https://checkout.paycom.uz/";

        String params = "m=" + merchantId +
                ";" +
                "ac.order_id=" + order.getId() +
                ";" +
                "a=" + order.getTotalPrice() * 100;

        String encodedParams = Base64.getEncoder().encodeToString(params.getBytes());

        return url + encodedParams;
    }

    public JSONObject payWithPaycom(PaycomRequestForm requestForm, String authorization) {
        JSONRPC2Response response = new JSONRPC2Response(requestForm.getId());

        //BASIC AUTH BO'SH BO'LSA YOKI XATO KELGAN BO'LSA ERROR RESPONSE BERAMIZ
        if (authorization == null || checkPaycomUserAuth(authorization, response)) {
            response.setError(new JSONRPC2Error(-32504,
                    "Error authentication",
                    "auth"));
            return response.toJSONObject();
        }

        //PAYCOM QAYSI METHODDA KELAYOTGANLIGIGA QARAB ISH BAJARAMIZ
        switch (requestForm.getMethod()) {
            case "CheckPerformTransaction":
                checkPerformTransaction(requestForm, response);
                break;
            case "CreateTransaction":
                createTransaction(requestForm, response);
                break;
            case "PerformTransaction":
                performTransaction(requestForm, response);
                break;
            case "CheckTransaction":
                checkTransaction(requestForm, response);
                break;
            case "GetStatement":
                getStatement(requestForm, response);
                break;
            case "CancelTransaction":
                cancelTransaction(requestForm, response);
                break;
            default:
                response.setError(new JSONRPC2Error(
                        -32601,
                        "Method not found",
                        requestForm.getMethod()));
                break;
        }

        return response.toJSONObject();
    }

    private void cancelTransaction(PaycomRequestForm requestForm, JSONRPC2Response response) {
        String transactionId = requestForm.getParams().getId();
        Optional<OrderTransaction> optionalOrderTransaction = orderTransactionRepository.findByTransactionId(transactionId);

        if (optionalOrderTransaction.isEmpty()) {
            response.setError(new JSONRPC2Error(
                    -31003,
                    "Transaction not found",
                    "transaction"));
            return;
        }

        OrderTransaction orderTransaction = optionalOrderTransaction.get();

        if (orderTransaction.getState().equals(TransactionState.STATE_DONE.getCode())) {
            response.setError(new JSONRPC2Error(
                    -31007,
                    "Unable to cancel transaction. Transaction already performed.",
                    "transaction"));
            return;
        }

        // Already cancelled
        if (orderTransaction.getState().equals(TransactionState.STATE_CANCELED.getCode())) {
            response.setResult(new ResultForm(
                    orderTransaction.getCancelTime() != null ? orderTransaction.getCancelTime() : 0,
                    null,
                    orderTransaction.getPerformTime() != null ? orderTransaction.getPerformTime() : 0,
                    orderTransaction.getReason(),
                    orderTransaction.getState(),
                    orderTransaction.getTransactionId()));
            return;
        }

        // Cancel if trans STATE_IN_PROGRESS
        if (orderTransaction.getState().equals(TransactionState.STATE_IN_PROGRESS.getCode())) {
            orderTransaction.setState(TransactionState.STATE_CANCELED.getCode());
            orderTransaction.setCancelTime(System.currentTimeMillis());

            if (requestForm.getParams().getReason() != null) {
                orderTransaction.setReason(requestForm.getParams().getReason());
            } else {
                orderTransaction.setReason(1);
            }

            orderTransactionRepository.save(orderTransaction);

            response.setResult(new ResultForm(
                    orderTransaction.getCancelTime(),
                    null,
                    orderTransaction.getPerformTime() != null ? orderTransaction.getPerformTime() : 0,
                    orderTransaction.getReason(),
                    orderTransaction.getState(),
                    orderTransaction.getTransactionId()));
        }
    }

    public boolean checkPerformTransaction(PaycomRequestForm requestForm, JSONRPC2Response response) {

        //PAYCOMDAN ACOUNT FIELDI KELMASA
        if (requestForm.getParams().getAccount() == null) {
            response.setError(new JSONRPC2Error(
                    -31050,
                    "Account field not found",
                    "account"
            ));
            return false;
        }

        //PAYCOMDAN ACOUNT FIELDI KELMASA
        if (requestForm.getParams().getAccount().getOrder() == null) {
            response.setError(new JSONRPC2Error(
                    -31050,
                    "Order not found",
                    "order"
            ));
            return false;
        }

        //PAYCOMDAN AMOUNT FIELDI NULL YOKI 0 KELSA
        if (requestForm.getParams().getAmount() == null || requestForm.getParams().getAmount() == 0) {
            response.setError(new JSONRPC2Error(
                    -31001,
                    "Amount error or null",
                    "amount"));
            return false;
        }

        //PAYCOM DAN KELGAN ORDER ID ORQALI ORDERNI OLAMIZ
        Optional<Order> optionalOrder = orderRepository.findById(requestForm.getParams().getAccount().getOrder());

        //AGAR ORDER BO'LSA
        if (optionalOrder.isEmpty()) {
            response.setError(new JSONRPC2Error(
                    -31050,
                    "Order not found",
                    "order"));
            return false;
        }

        //ORDER SUM BILAN PAYCOMDAN KELGAN SUM TENGLIGI TEKSHIRILYAPTI
        Order order = optionalOrder.get();
        if (!order.getTotalPrice().equals(requestForm.getParams().getAmount())) {
            response.setError(new JSONRPC2Error(
                    -31001,
                    "Wrong amount",
                    "amount"));
            return false;
        }

        //ORDER CANCEL BO'LGAN YOKI YO'QLIGINI TEKSHRIAMIZ
        if (order.isCancelled()) {
            response.setError(new JSONRPC2Error(
                    -31099,
                    "Order already cancelled",
                    "order"));
            return false;
        }

        //ORDER ALLAQACHON YAKUNLANAGAN BO'LSA
        if (order.isPaid()) {
            response.setError(new JSONRPC2Error(
                    -31099,
                    "Order already finished",
                    "order"));
            return false;
        }

        response.setResult(new CheckPerformTransactionAllowResponse(
                new AdditionalInfo(order.getId(), order.getTotalPrice() * 100),
                true));
        return true;
    }


    /**
     * YANGI TRANSACTION OCHISH UCHUN YOKI ESKISI BO'LSA UNI MUDDATI O'TMAGANLIGINI TEKSHIRAMIZ
     * https://developer.help.paycom.uz/ru/metody-merchant-api/createtransaction
     *
     * @param requestForm @RequestBody
     * @param response    JSONRPC2Response
     */
    private void createTransaction(PaycomRequestForm requestForm, JSONRPC2Response response) {

        //PAYCOM DAN KELGAN ID BO'YICHA TRASACTION OLYAPMIZ
        Optional<OrderTransaction> optionalOrderTransaction = orderTransactionRepository.findByTransactionId(requestForm.getParams().getId());

        OrderTransaction orderTransaction;

        //AGAR OrderTransaction AVVAL YARATILGAN BO'LSA
        if (optionalOrderTransaction.isPresent()) {
            orderTransaction = optionalOrderTransaction.get();

            //OrderTransaction STATE IN PROGRESS DA BO'LMASA XATO QAYTARAMIZ
            if (!orderTransaction.getState().equals(TransactionState.STATE_IN_PROGRESS.getCode())) {
                response.setError(new JSONRPC2Error(
                        -31008,
                        "Unable to complete operation",
                        "transaction"));
                return;
            }

            //OrderTransaction YARATILGAN VAQTI 12 SOATDAN  KO'P BO'LSA XATO QAYTARAMIZ. MUDDATI O'TGAN ORDER
            if (System.currentTimeMillis() - orderTransaction.getTransactionCreationTime() > TIME_EXPIRED_PAYCOM_ORDER) {
                response.setError(new JSONRPC2Error(
                        -31008,
                        "Unable to complete operation",
                        "transaction"));

                //ORDER_TRANSACTION NI O'ZGARTIRIB SAQLAB QO'YAMIZ
                orderTransaction.setReason(4);
                orderTransaction.setState(TransactionState.STATE_CANCELED.getCode());
                orderTransactionRepository.save(orderTransaction);
                return;
            }
        }

        //OrderTransaction YARATILMAGAN BO'LSA
        else {

            //ORDER HAMMA JIHATDAN TO'G'RILIGINI TEKSHIRAMIZ
            boolean checkPerformTransaction = checkPerformTransaction(requestForm, response);

            //AGAR ORDER XATO BO'LSA XATONI YUBORAMIZ
            if (!checkPerformTransaction) {
                return;
            }

            Long orderId = requestForm.getParams().getAccount().getOrder();

            Optional<OrderTransaction> byOrderId = orderTransactionRepository.findByOrderId(orderId);

            if (byOrderId.isPresent()) {
                response.setError(new JSONRPC2Error(
                        -31099,
                        "This order already have transaction",
                        "transaction"));
                return;
            }


            //YANGI OrderTransaction
            orderTransaction = new OrderTransaction(
                    requestForm.getParams().getId(),
                    requestForm.getParams().getTime(),
                    TransactionState.STATE_IN_PROGRESS.getCode(),
                    orderId);

            orderTransactionRepository.save(orderTransaction);
        }

        //AVVAL SAQLANGAN MUDDATO O'TMAGAN OrderTransaction YOKI YANGI SAQLANGAN OrderTransaction NING MA'LUMOTLARI QAYTARILYAPTI
        response.setResult(new ResultForm(
                orderTransaction.getTransactionCreationTime(),
                orderTransaction.getState(),
                requestForm.getParams().getId()));
    }


    /**
     * TO'LOVNI AMALGA OSHIRADIGAN METHOD
     * https://developer.help.paycom.uz/ru/metody-merchant-api/performtransaction
     *
     * @param requestForm @RequestBody
     * @param response    JSONRPC2Response
     */
    private void performTransaction(PaycomRequestForm requestForm, JSONRPC2Response response) {

        //PAYCOM DAN KELGAN ID BO'YICHA OrderTransaction NI QIDIRAMIZ
        Optional<OrderTransaction> optionalOrderTransaction = orderTransactionRepository.findByTransactionId(requestForm.getParams().getId());

        //AGAR OrderTransaction TOPILMASA XATOLIK QAYTARAMIZ
        if (optionalOrderTransaction.isEmpty()) {
            response.setError(new JSONRPC2Error(
                    -31003,
                    "Order transaction not found",
                    "transaction"));
            return;
        }

        OrderTransaction orderTransaction = optionalOrderTransaction.get();

        //OrderTransaction NING STATE IN_PROGRESS(1) BO'LSA
        if (orderTransaction.getState().equals(TransactionState.STATE_IN_PROGRESS.getCode())) {

            //OrderTransaction YARATILGAN VAQTI 12 SOATDAN  KO'P BO'LSA XATO QAYTARAMIZ. MUDDATI O'TGAN ORDER
            if (System.currentTimeMillis() - orderTransaction.getTransactionCreationTime() > TIME_EXPIRED_PAYCOM_ORDER) {
                response.setError(new JSONRPC2Error(
                        -31008,
                        "Unable to complete operation",
                        "transaction"));

                //ORDER_TRANSACTION NI O'ZGARTIRIB SAQLAB QO'YAMIZ
                orderTransaction.setReason(4);
                orderTransaction.setState(TransactionState.STATE_CANCELED.getCode());
                orderTransactionRepository.save(orderTransaction);
                return;
            }

            orderTransaction.setState(TransactionState.STATE_DONE.getCode());
            orderTransaction.setPerformTime(System.currentTimeMillis());
            orderTransactionRepository.save(orderTransaction);

            //TO'LOV BO'LDI

            Order order = orderTransaction.getOrder();
            order.setPaid(true);
            order.setInvoiceId(orderTransaction.getTransactionId());
            orderRepository.save(order);
        }

        //OrderTransaction GA TO'LOV QILINIB YAKUNIGA YETGAN BO'LSA
        if (orderTransaction.getState().equals(TransactionState.STATE_DONE.getCode())) {
            response.setResult(new ResultForm(
                    null,
                    null,
                    orderTransaction.getPerformTime(),
                    null,
                    orderTransaction.getState(),
                    orderTransaction.getTransactionId()));
            return;
        }

        //OrderTransaction NING STATE DONE(2) BO'LMASA XATOLIK BERAMIZ
        response.setError(new JSONRPC2Error(
                -31008,
                "Unable to complete operation",
                "transaction"));
    }


    /**
     * TRANSACTION HOLATINI BILISH UCHUN METHOD
     * https://developer.help.paycom.uz/ru/metody-merchant-api/checktransaction
     *
     * @param requestForm @RequestBody
     * @param response    JSONRPC2Response
     */
    private void checkTransaction(PaycomRequestForm requestForm, JSONRPC2Response response) {
        Optional<OrderTransaction> optionalOrderTransaction = orderTransactionRepository.findByTransactionId(requestForm.getParams().getId());

        if (optionalOrderTransaction.isEmpty()) {
            response.setError(new JSONRPC2Error(
                    -31003,
                    "Order transaction not found",
                    "transaction"));
            return;
        }

        OrderTransaction orderTransaction = optionalOrderTransaction.get();

        response.setResult(new ResultForm(
                orderTransaction.getCancelTime() != null ? orderTransaction.getCancelTime() : 0,
                orderTransaction.getTransactionCreationTime(),
                orderTransaction.getPerformTime() != null ? orderTransaction.getPerformTime() : 0,
                orderTransaction.getReason(),
                orderTransaction.getState(),
                orderTransaction.getTransactionId()));
    }

    /**
     * PAYCOM TOMONIDAN MUVAFFAQIYATLI BAJRILGAN BARCHA OrderTransaction LARNI QAYTARAMIZ
     * https://developer.help.paycom.uz/ru/metody-merchant-api/getstatement
     *
     * @param requestForm @RequestBody
     * @param response    JSONRPC2Response
     */
    private void getStatement(PaycomRequestForm requestForm, JSONRPC2Response response) {

        //DB DAN PAYCOM BERGAN VAQT OALIG'IDA TRANSACTION STATE DONE(2) BO'LGAN OrderTransaction LAR OLINADI
        List<OrderTransaction> orderTransactionList = orderTransactionRepository
                .findAllByTransactionCreationTimeBetweenOrderByTransactionCreationTimeAsc(
                        requestForm.getParams().getFrom(),
                        requestForm.getParams().getTo());

        List<Transaction> transactions = new ArrayList<>();

        //OrderTransaction LARDAN Transaction OBJECTIGA MAP QILINADI
        for (OrderTransaction orderTransaction : orderTransactionList) {
            Transaction transaction = new Transaction();
            transaction.setId(orderTransaction.getTransactionId());
            transaction.setAccount(new Account(orderTransaction.getOrderId()));
            transaction.setAmount(orderTransaction.getOrder().getTotalPrice() * 100);
            transaction.setCreateTime(orderTransaction.getTransactionCreationTime());
            transaction.setTime(orderTransaction.getTransactionCreationTime());
            transaction.setPerformTime(orderTransaction.getPerformTime() != null ?
                    orderTransaction.getPerformTime() : 0L);
            transaction.setCancelTime(orderTransaction.getCancelTime() != null ?
                    orderTransaction.getCancelTime() : 0L);
            transaction.setState(orderTransaction.getState());
            transaction.setReason(orderTransaction.getReason());
            transaction.setTransaction(orderTransaction.getOrderId().toString());

            transactions.add(transaction);
        }

        //PAYCOMGA Transaction LISTI YUBORILADI
        Map<String, Object> result = new HashMap<>();
        result.put("transactions", transactions);
        response.setResult(result);
    }

    /**
     * PAYCOM DAN KELGAN BASIC AUTHNI TEKSHIRAMIZ
     *
     * @param basicAuth String
     * @param response  JSONRPC2Response
     * @return boolean
     */
    private boolean checkPaycomUserAuth(String basicAuth, JSONRPC2Response response) {

        basicAuth = basicAuth.substring("Basic".length()).trim();

        byte[] decode = Base64.getDecoder().decode(basicAuth);

        basicAuth = new String(decode, Charset.defaultCharset());

        String[] split = basicAuth.split(":", 2);

        if (split[0].equals("Paycom")){

            return !split[1].equals(secretKeyTest) && !split[1].equals(secretKeyProd);

        }
        return true;
    }

}
