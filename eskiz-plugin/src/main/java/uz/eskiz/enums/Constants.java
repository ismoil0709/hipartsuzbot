package uz.eskiz.enums;

public class Constants {

    public static final String BASE_URL = "https://notify.eskiz.uz";
    public static final String BEARER_TOKEN = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";



    /*Eskiz APIs*/

    public static final String GET_USER_DATA = "/api/auth/user";
    public static final String GET_TOKEN = "/api/auth/login";
    public static final String REFRESH_TOKEN = "/api/auth/refresh";
    public static final String GET_SMS_TEMPLATE = "/api/user/templates";
    public static final String SEND_SMS = "/api/message/sms/send";
    public static final String SEND_SMS_DISPATCH = "/api/message/sms/send-batch";
    public static final String SEND_TEMPLATE = "/api/user/template";
    public static final String GET_SMS_STATUS_BY_DISPATCH_ID = "/api/message/sms/get-user-messages-by-dispatch";
    public static final String GET_SMS_STATUS_BY_SMS_ID = "/api/message/sms/status_by_id/";
    public static final String GET_REPORT_TOTAL_BY_YEAR_MONTH = "/api/user/totals";
    public static final String GET_REPORT_BY_YEAR = "/api/report/total-by-month?year=";

}
