package pjwstk.tpo_6.mingle.Utils;

public class ErrorsManager {
    public static String getMessageByCode(int code){
        switch(code){
            case 400:
                return "Bad request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Page not found";
            case 500:
                return "Internal server error";
            default:
                return "Unknown error";
        }
    }
}
