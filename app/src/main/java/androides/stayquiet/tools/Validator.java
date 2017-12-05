package androides.stayquiet.tools;

/**
 * Created by developer on 23/11/17.
 */

public class Validator {
    private static String regexpUsername = "^[a-zA-Z]+[0-9]*[a-zA-Z]*";
    private static String regexpName = "^[a-zA-Z\\s]+";
    private static String regexpPhoneNumber = "[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]";
    private static String regexpPassword = "(/^(?=.*[a-z]).+$/)(/^(?=.*[A-Z]).+$/)(/^(?=.*[0-9_\\W]).+$/)";
    private static String regexpCode = "[0-9][0-9][0-9][0-9][0-9][0-9]";

    public static boolean usernameIsValid(String input) {
        return input.matches(regexpUsername);
    }

    public static boolean nameIsValid(String input) {
        return input.matches(regexpName);
    }

    public static boolean phoneNumberIsValid(String input) {
        return input.matches(regexpPhoneNumber);
    }

    public static boolean emailIsValid(String input) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    public static boolean passwordIsValid(String input) {
        //return input.matches(regexpPassword);
        return true;
    }

    public static boolean codeIsValid(String input){
        return  input.matches(regexpCode);
    }
}
