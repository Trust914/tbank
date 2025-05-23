package com.tbank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public final class Validation {
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 20;
    private static final int MAX_USERNAME_LENGTH = 8;
    private static final Pattern USERNAME_REGEX = Pattern.compile(String.format("^[a-zA-Z0-9]{%d,}$",MIN_NAME_LENGTH));
    private static final Pattern PASSWORD_REGEX = Pattern.compile(String.format("^(?=.*[a-zA-Z])(?=.*[0-9]).{%d,}$", MIN_PASSWORD_LENGTH));
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?``{|}~^.-]+@[a-zA-Z0-9.-]+$");
    private static final ArrayList<String> ALLOWED_ROLES = new ArrayList<>();

    static{
        ALLOWED_ROLES.add("admin");
        ALLOWED_ROLES.add("customer");
    }

    private Validation(){
        throw new  AssertionError("Cannot instantiate utility class");
    }

    public static boolean validateLogin(HashMap<String, String> loginSchema){
        boolean isValid = true;
        String errorMessage = "";

        if (loginSchema == null || loginSchema.isEmpty()){
            errorMessage = "No login data recieved";
            AppLogger.error(errorMessage);
            return !isValid;
        }

        for(String field: loginSchema.keySet()){
            String value = loginSchema.get(field);
            switch (field) {
                case "username" -> {
                    isValid = !value.isEmpty() && value.length() >= MIN_NAME_LENGTH && value.length() <= MAX_USERNAME_LENGTH && USERNAME_REGEX.matcher(value).matches();
                    if (!isValid){
                        errorMessage += String.format("%s cannot be empty and must have minimum of %d characters, maximum of %d characters and must contain only letters and numbers\n",field,MIN_NAME_LENGTH, MAX_USERNAME_LENGTH);
                    }
                }
                case "password" -> {
                    isValid = !value.isEmpty() && PASSWORD_REGEX.matcher(value).matches();
                    if (!isValid){
                        errorMessage += String.format("%s cannot be empty and must have minimum of %d characters and must contain at least one letter and one number\n",field,MIN_PASSWORD_LENGTH);
                    }
                }
                case "role" -> {
                    isValid = !value.isEmpty() && ( value.equals("admin") || value.equals("customer"));
                    if (!isValid){
                        errorMessage += String.format("%s cannot be empty and must be in %s\n",field,ALLOWED_ROLES);
                    }
                }
                default -> {
                    isValid = false;
                    errorMessage += String.format("The field %s is not allowed\n",field);
                }
            }
        }
        if (!isValid){
            AppLogger.error(errorMessage);
            return false;
        }else{
            return true;
        }

    }

    public static boolean validateRegistration(HashMap<String, String> registerSchema){
        boolean isValid = true;
        String errorMessage = "";

        if (registerSchema == null || registerSchema.isEmpty()){
            errorMessage = "No registration data recieved";
            AppLogger.error(errorMessage);
            return false;
        }
        for(String field: registerSchema.keySet()){
            String value = registerSchema.get(field);
            // System.out.println(field);

            switch (field) {
                case "firstName" -> {
                    isValid = !value.isEmpty() && value.length() >= MIN_NAME_LENGTH && value.length() <= MAX_NAME_LENGTH;
                    if (!isValid){
                        errorMessage += String.format("%s cannot be empty and must be between %d and %d characters\n",field,MIN_NAME_LENGTH,MAX_NAME_LENGTH);
                    }
                }
                case "lastName" -> {
                    isValid = !value.isEmpty() && value.length() >= MIN_NAME_LENGTH && value.length() <= MAX_NAME_LENGTH;
                    if (!isValid){
                        errorMessage += String.format("%s cannot be empty and must be between %d and %d characters\n",field,MIN_NAME_LENGTH,MAX_NAME_LENGTH);
                    }
                }
                case "username" -> {
                    isValid = !value.isEmpty() && value.length() >= MIN_NAME_LENGTH && value.length() <= MAX_USERNAME_LENGTH && USERNAME_REGEX.matcher(value).matches();
                    if (!isValid){
                        errorMessage += String.format("%s cannot be empty and must have minimum of %d characters, maximum of %d characters and must contain only letters and numbers\n",field,MIN_NAME_LENGTH, MAX_USERNAME_LENGTH);
                    }
                }
                case "email" ->{
                    isValid = !value.isEmpty() && EMAIL_REGEX.matcher(value).matches();
                    if (!isValid){
                        errorMessage += String.format("%s cannot be empty and must be a valid email value\n",field);
                    }
                }
                case "password" -> {
                    isValid = !value.isEmpty() && PASSWORD_REGEX.matcher(value).matches();
                    if (!isValid){
                        errorMessage += String.format("%s cannot be empty and must have minimum of %d characters and must contain at least one letter and one number\n",field,MIN_PASSWORD_LENGTH);
                    }
                }
                case "role" -> {
                    isValid = !value.isEmpty() && ( value.equals("admin") || value.equals("customer"));
                    if (!isValid){
                        errorMessage += String.format("%s cannot be empty and must be in %s\n",field,ALLOWED_ROLES);
                    }
                }
                default -> {
                    isValid = false;
                    errorMessage += String.format("The field %s is not allowed\n",field);
                }
            }
        }
        if (!isValid){
            AppLogger.error(errorMessage);
            return false;
        }else{
            return true;
        }
    }
}