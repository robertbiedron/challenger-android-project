package com.alobha.challenger.data.api.helpers;

/**
 * Created by mrNRG on 10.06.2016.
 */
public class StatusCodes {

    private static final String SUCCESS_ACTION = "Success action";
    private static final String FORGOT_TO_SEND_EMAIL = "You forgot to send \"Email\" value";
    private static final String USER_WITH_THAT_EMAIL_EXIST = "User with that \"Email\" is already exist";
    private static final String EMAIL_CAN_T_BE_LESS_THAN = "\"Email\" can't be less than 6 characters";
    private static final String EMAIL_CAN_T_BE_MORE_THAN = "\"Email\" can't be more that 30 characters";
    private static final String EMAIL_CAN_T_CONTAIN_WHITESPACES = "\"Email\" can't contain whitespace(s)";
    private static final String WRONG_EMAIL_FORMAT = "\"Email\" not in Email-decimalFormat like \"qwerty@gmail.com\"";
    private static final String PASS_CAN_T_BE_LESS_THAN = "\"Password\" can't be less than 6 characters";
    private static final String PASS_CAN_T_BE_MORE_THAN = "\"Password\" can't be more than 20 characters";
    private static final String PASS_CAN_T_CONTAIN_WHITESPACES = "\"Password\" can't contain whitespace(s)";
    private static final String USER_WITH_THAT_TOKEN_DOESN_T_EXIST = "User with that \"token\" doesn't exist";
    private static final String FORGOT_TO_SEND_PHONE = "You forgot to send \"phone\" value";
    private static final String FORGOT_TO_SEND_PASS = "You forgot to send \"password\" value";
    private static final String PHONE_CAN_T_BE_LESS_THAN = "\"Phone\" can't be less than 9 characters";
    private static final String PHONE_CAN_T_BE_MORE_THAN = "\"Phone\" can't be more than 15 characters";
    private static final String FORGOT_TO_SEND_SEX = "You forgot to send \"sex\" value";
    private static final String INCORRECT_SEX_FORMAT = "You send \"sex\" in incorrect decimalFormat. Please send \"Male\" or \"Female\" value";
    private static final String FORGOT_TO_SEND_ACCESS_TOKEN_FROM_FACEBOOK = "You forgot to send \"access_token\" from facebook account";
    private static final String USER_WITH_THAT_PHONE_IS_ALREADY_EXIST = "User with that \"phone\" is already exist";
    private static final String INCORRECT_ACCESS_TOKEN_FROM_FACEBOOK = "You send incorrect \"access_token\" from facebook account";
    private static final String PHONE_CAN_T_CONTAIN_WHITESPACES = "\"Phone\" can't contain whitespace(s)";
    private static final String INCORRECT_PASS = "Incorrect \"password\"";
    private static final String USER_WITH_THAT_EMAIL_DOESN_T_EXIST = "User with that \"email\" doesn't exist";
    private static final String INTERNET_CONNECTION_ERROR = "Internet connection error";

    public static String statusMessage(int statusCode) {
        String statusMessage = "";
        switch (statusCode) {
            case 0:
                statusMessage = SUCCESS_ACTION;
                break;
            case 1:
                statusMessage = FORGOT_TO_SEND_EMAIL;
                break;
            case 2:
                statusMessage = USER_WITH_THAT_EMAIL_EXIST;
                break;
            case 3:
                statusMessage = EMAIL_CAN_T_BE_LESS_THAN;
                break;
            case 4:
                statusMessage = EMAIL_CAN_T_BE_MORE_THAN;
                break;
            case 5:
                statusMessage = EMAIL_CAN_T_CONTAIN_WHITESPACES;
                break;
            case 6:
                statusMessage = WRONG_EMAIL_FORMAT;
                break;
            case 7:
                statusMessage = PASS_CAN_T_BE_LESS_THAN;
                break;
            case 8:
                statusMessage = PASS_CAN_T_BE_MORE_THAN;
                break;
            case 9:
                statusMessage = PASS_CAN_T_CONTAIN_WHITESPACES;
                break;
            case 10:
                statusMessage = USER_WITH_THAT_TOKEN_DOESN_T_EXIST;
                break;
            case 11:
                statusMessage = FORGOT_TO_SEND_PHONE;
                break;
            case 12:
                statusMessage = FORGOT_TO_SEND_PASS;
                break;
            case 13:
                statusMessage = PHONE_CAN_T_BE_LESS_THAN;
                break;
            case 14:
                statusMessage = PHONE_CAN_T_BE_MORE_THAN;
                break;
            case 15:
                statusMessage = FORGOT_TO_SEND_SEX;
                break;
            case 16:
                statusMessage = INCORRECT_SEX_FORMAT;
                break;
            case 17:
                statusMessage = FORGOT_TO_SEND_ACCESS_TOKEN_FROM_FACEBOOK;
                break;
            case 18:
                statusMessage = USER_WITH_THAT_PHONE_IS_ALREADY_EXIST;
                break;
            case 19:
                statusMessage = INCORRECT_ACCESS_TOKEN_FROM_FACEBOOK;
                break;
            case 20:
                statusMessage = PHONE_CAN_T_CONTAIN_WHITESPACES;
                break;
            case 22:
                statusMessage = INCORRECT_PASS;
                break;
            case 23:
                statusMessage = USER_WITH_THAT_EMAIL_DOESN_T_EXIST;
                break;
            default:
                statusMessage = INTERNET_CONNECTION_ERROR;
                break;
        }

        return statusMessage;
    }
}
