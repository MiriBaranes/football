package com.ashcollege.validator;

public class EmailValidator {


    public static boolean isValidEmail(String email) {
        if (!email.contains(" ")) {
            String[] split = (email.split("@"));
            if (split.length == 2) {
                if (split[1].contains(".")) {
                    String[] split2 = split[1].split("\\.");
                    return split2.length == 2;
                }
            }
        }
        return false;
    }
}
