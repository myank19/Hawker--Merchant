package io.goolean.tech.hawker.merchant.Constant;

public class Urls {
    //    private static final String ROOT_URL = "http://13.233.216.129/Hawker/index.php/seller/Api";
    private static final String ROOT_URL = "http://35.154.190.204/Hawker/index.php/seller/Api";
     //private static final String ROOT_URL = "http://hawker.goolean.com/hawker/index.php/seller/Api";
    public static final String URL_APP_VERSION_CHECK = ROOT_URL + "/app_check_version";
    public static final String URL_LOGIN = ROOT_URL + "/login";
    public static final String URL_OTP_SUBMIT = ROOT_URL + "/verification_otp_data";
    public static final String URL_OTP_RESEND = ROOT_URL + "/resend_otp_data";
    public static final String URL_OTP_EXPIRE = ROOT_URL + "/otp_expire";
    public static final String URL_SELLER_LOGOUT = ROOT_URL + "/data_logout_for_seller";
    public static final String URL_SELLER_PROFILE = ROOT_URL + "/seller_profile";
    public static final String URL_HINDI = "http://35.154.190.204/Hawker/hindi.php";
    public static final String URL_DUTY_ON_OFF_SELLER = ROOT_URL + "/duty_on_off_by_seller";
    public static final String URL_SELLER_CURRENT_LOCATION = ROOT_URL + "/seller_location_by_gps";
    public static final String URL_LOGOUT_FROM_OTHER_DEVICE = ROOT_URL + "/status_check_data";
    public static final String URL_CUSTOMERS_LOCATION = ROOT_URL + "/navigate_customer_data";
    public static final String URL_CLOSE_TRIP = ROOT_URL + "/close_share_location_by_customer";
    public static final String URL_TRIP_HISTORY = ROOT_URL + "/navigate_customer_history_data";
    public static final String URL_REFERRAL_MONEY = ROOT_URL + "/count_hawker_referral_code";
    public static final String URL_RECEIVED_REFERRAL_MONEY = ROOT_URL + "/received_referral_money";
    public static final String URL_RECEIVED_REFERRAL_MONEY_HISTORY = ROOT_URL + "/history_detail_for_received_money_hawker";
    public static final String URL_REQUEST_HAWKER_ADVERTISEMENT = ROOT_URL + "/request_for_hawker_advertisement";
    public static final String URL_REQUEST_HAWKER_PAID_ADVERTISEMENT = ROOT_URL + "/insert_paid_advertisement";
    public static final String URL_REQUEST_HAWKER_PAID_REQUEST = ROOT_URL + "/paymentTxn";
    public static final String URL_PLAN_TYPE = ROOT_URL + "/getPlanType";
    public static final String URL_DUTY_ONOFF = ROOT_URL + "/locationOffDutyOff";
}
