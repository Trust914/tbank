package com.tbank;

public class TBankMain {

    private static final String LOGO = String.format("""
                                       %s$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                                       $$$                                              $$$
                                       $$$              THIS IS THE TBANK               $$$
                                       $$$                                              $$$
                                       $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$%s
                                       """,Colour.ART,Colour.RESET);
    public static void main(String[] args) {
        while (true) {
            Util.clearConsole(false);
            showLogo();
            String prompt = """ 
                    ╔══════════════════════════════════════════════════╗
                    ║                     TBANK                        ║
                    ╠══════════════════════════════════════════════════╣
                    ║                     1. Customer                  ║
                    ║                     2. Admin                     ║
                    ║                     3. Exit                      ║
                    ║                                                  ║
                                  -> Choose an option """;
            Integer[] allowedOptions = {1,2,3};
            int choice = Util.getIntInput(prompt,allowedOptions);
            UserRepository userRepository;
            switch (choice) {
                case 1 -> { 
                    userRepository = new UserRepository("customer", LOGO);
                    userRepository.Main();
                }
                case 2 -> {
                    userRepository = new UserRepository("admin", LOGO);
                    userRepository.Main();
                }
                case 3 -> {
                    showGoodBye();
                    Util.pause();
                    Util.closeScanner();
                    return;
                }
            }
        }
    }

    private static void showLogo(){
        System.out.println(LOGO);
    }

    private static void showGoodBye(){
        System.out.printf("""
                           %s$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                                                  GOODBYE   
                           $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$%s            
                           """,Colour.ART,Colour.RESET);
    }
}
