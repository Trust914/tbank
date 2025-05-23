package com.tbank;

import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public final class Util {
    private static final Scanner scanner = new Scanner(System.in);
    private static final int PAUSE_TIME_MILLI = 1000;

    private Util(){
        throw new  AssertionError("Cannot instantiate utility class");
    }

    public static void pause() {
        try {
            Thread.sleep(PAUSE_TIME_MILLI);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    public static  void  clearConsole(boolean pauseBefore){
        if(pauseBefore){
            pause();
        }
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException e) {
            AppLogger.error(e.toString());
        }
    }

    private static <T> boolean isValidOption(T input, T[] allowedOptions) {
        if (allowedOptions == null || allowedOptions.length == 0) {
            return true;
        }
        
        for (T option : allowedOptions) {
            if (option.equals(input)) { 
                return true;
            }
        }
        return false;
    }

    public static int getIntInput(String prompt, Integer[] allowedOptions) {
        String input;
        String message;
        int value;

        System.out.print(prompt + ": ");
        input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            AppLogger.error("You must enter a value to proceed!!");
            return getIntInput(prompt, allowedOptions);
        }
        
        try {
            value = Integer.parseInt(input);
            
            if (allowedOptions != null && allowedOptions.length > 0) {
                
                if (isValidOption(value, allowedOptions)) {
                    return value;
                } else {
                    Arrays.sort(allowedOptions);
                    message = String.format("Your input %d is not supported, please enter a value between %d and %d", 
                                        value, allowedOptions[0], allowedOptions[allowedOptions.length - 1]);
                    AppLogger.error(message);
                    return getIntInput(prompt, allowedOptions);
                }
            } else {
                return value;
            }
        } catch (NumberFormatException e) {
            message = "Only numbers are supported!";
            AppLogger.error(message);
            return getIntInput(prompt, allowedOptions);
        }
    }

    public static  double getDoubleInput(String prompt, Double[] allowedOptions){
        String input;
        String message;
        double value;

        System.out.print(prompt + ": ");
        input = scanner.nextLine().trim();
        if (input.isEmpty()){
            AppLogger.error("You must enter a value to proceed!!");
            return getDoubleInput(prompt,allowedOptions);
        }
        try {
            value = Double.parseDouble(input);
            if (allowedOptions != null && allowedOptions.length > 0){
                if (isValidOption(value, allowedOptions)){
                    return value;
                }else{
                    Arrays.sort(allowedOptions);
                    message = String.format("Your input %.2f is not supported, please enter a value between %.2f and %.2f", 
                                            value,allowedOptions[0], allowedOptions[allowedOptions.length - 1]);
                    AppLogger.error(message);
                    return getDoubleInput(prompt,allowedOptions);
                }
        
            }else{
                return value;
            }
        } catch (NumberFormatException  e) {
            message = "Only numbers are supported!";
            AppLogger.error(message);
            return getDoubleInput(prompt,allowedOptions);
        }        
    }

    public static  long getLongInput(String prompt, Long[] allowedOptions){
        String input;
        String message;
        long value;

        System.out.print(prompt + ": ");
        input = scanner.nextLine().trim();
        if (input.isEmpty()){
            AppLogger.error("You must enter a value to proceed!!");
            return getLongInput(prompt,allowedOptions);
        }
        try {
            value = Long.parseLong(input);
            if (allowedOptions != null && allowedOptions.length > 0){
                if (isValidOption(value, allowedOptions)){
                    return value;
                }else{
                    Arrays.sort(allowedOptions);
                    message = String.format("Your input %d is not supported, please enter a value between %d and %d", 
                                            value,allowedOptions[0], allowedOptions[allowedOptions.length - 1]);
                    AppLogger.error(message);
                    return getLongInput(prompt,allowedOptions);
                }
        
            }else{
                return value;
            }
        } catch (NumberFormatException  e) {
            message = "Only numbers are supported!";
            AppLogger.error(message);
            return getLongInput(prompt,allowedOptions);
        }        
    }

    public static  char getCharInput(String prompt, Character[] allowedOptions){
        String input;
        char value;
        String message;

        System.out.print(prompt + ": ");
        input = scanner.nextLine().trim();
        if (input.isEmpty() ){
            AppLogger.error("You must enter a value to proceed");
            return getCharInput(prompt, allowedOptions);
        }
        try {
            if (input.length() > 1){
                AppLogger.error("Only single characters are supported!");
                return getCharInput(prompt, allowedOptions);
            }else{
                value = input.charAt(0);
                if (allowedOptions != null && allowedOptions.length > 0){
                    if (isValidOption(value, allowedOptions)){
                        return value;
                    }else{
                        Arrays.sort(allowedOptions);
                        message = String.format("Your input %c is not supported, please enter a valid value from the allowedOptions",value);
                        AppLogger.error(message);
                        return getCharInput(prompt, allowedOptions);
                    }
                }else{
                    return value;
                }

            }
        } catch (InputMismatchException e) {
            message = "Only single characters are supported!";
            AppLogger.error(message);
            scanner.nextLine();
            return getCharInput(prompt,allowedOptions);
        }  
    }

    public static  String getStringInput(String prompt, String[] allowedOptions){
        String input;
        String message;

        System.out.print(prompt + ": ");
        input = scanner.nextLine().trim();
        if (input.isEmpty() ){
            AppLogger.error("You must enter a value to proceed");
            return getStringInput(prompt, allowedOptions);
        }
        try {
            if (allowedOptions != null && allowedOptions.length > 0){
                if (isValidOption(input, allowedOptions)){
                    return input;
                }else{
                    Arrays.sort(allowedOptions);
                    message = String.format("Your input %s is not supported, please enter a valid value from the allowedOptions",input);
                    AppLogger.error(message);
                    return getStringInput(prompt, allowedOptions);
                }
            }else{
                return input;
            }
        } catch (InputMismatchException e) {
            message = "Only strings are supported!";
            AppLogger.error(message);
            scanner.nextLine();
            return getStringInput(prompt,allowedOptions);
        }     
    }
}