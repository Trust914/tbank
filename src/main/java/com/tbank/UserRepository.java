package com.tbank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public  final class UserRepository{
    protected static final DB<UserModel> DB = new DB<>();
    enum Field{
        firstName,
        lastName,
        userName,
        password,
        email,
    }
    
    private final AccountRepository accountRepository = new AccountRepository();
    private final  String userRole;
    private final HashMap<String, Object> userDetails = new HashMap<>();
    private final String logo;

    private boolean isUserLoggedIn;

    //Constructor 
    protected UserRepository(String userRole, String logo){
        this.userRole = userRole;
        this.logo = logo;
    }

    // Getters
    protected  boolean getIsUserLoggedIn(){
        return this.isUserLoggedIn;
    }

    // Setters
    protected void setIsUserLoggedIn(boolean isUserLoggedIn){
        this.isUserLoggedIn = isUserLoggedIn;
    }

    protected void setUserDetails(UserModel user){
        if (this.isUserLoggedIn){
            this.userDetails.put("_id",user.getId());
            this.userDetails.put("username", user.getUsername());
        }
    }


    // Methods
    protected final void Main(){
        while (true) {
            Util.clearConsole(true); // Clear screen before showing the customer page
            this.showLogo();

            String prompt = String.format(""" 
                    ╔══════════════════════════════════════════════════╗
                                       %s PAGE                         
                    ╠══════════════════════════════════════════════════╣
                    ║                    1. Login                      ║
                    ║                    2. Register                   ║
                    ║                    3. Logout                     ║
                    ║                                                  ║
                                  -> Choose an option """,this.userRole.toUpperCase());
            Integer[] allowedOptions = {1,2,3};
            int choice = Util.getIntInput(prompt, allowedOptions);
            switch (choice){
                case 1 -> {
                    login();
                    if (this.isUserLoggedIn){
                        showWelcomeMessage(this.userDetails.get("username").toString());
                        if(this.isUserAdmin()){
                            adminOperations();
                        }else{
                           customerOperations(); 
                        }
                    }
                }
                case 2 -> {
                    UserModel newUser = register();
                    if (newUser != null){
                        Character[] loginResAllowed = {'Y','N','y','n'};
                        char loginRes = Character.toLowerCase(Util.getCharInput("\nProceed to login? (y/n) ", loginResAllowed));

                        if (loginRes == 'y'){
                            Util.clearConsole(false);
                            login();
                            if (this.isUserLoggedIn){
                                showWelcomeMessage(this.userDetails.get("username").toString());
                                if(this.isUserAdmin()){
                                    adminOperations();
                                }else{
                                    customerOperations(); 
                                }
                            }
                        }else{
                            return;
                        }
                        
                    }
                }
                case 3 -> {
                    logout();
                    return;
                }
            }
        }
    }

    private void showLogo(){
        System.out.println(this.logo);
    }

    private  boolean isUserAdmin(){
        return this.userRole.equals("admin");
    }

    private boolean isUserExist(String usernameOrEmail){
        usernameOrEmail = usernameOrEmail.toLowerCase();
        for (UserModel user : DB.getAll()){
            if(user.getUsername().equals(usernameOrEmail) || user.getEmail().equals(usernameOrEmail)){
                return true;
            }
        }
        return false;
    }

    private static void showGoodBye(String username){
        System.out.printf("""
                           %s$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                                            GOODBYE %s
                           $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$%s            
                           """,Colour.ART,username,Colour.RESET);
    }

    private static void showWelcomeMessage(String username){
        Util.clearConsole(true);
        System.out.printf("""
                           %s$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                                            WELCOME %s  
                           $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$%s            
                           """,Colour.ART,username,Colour.RESET);
    }

    private boolean isAuthenticatedUser(){
        if(DB.isEmpty()){
            AppLogger.error("No users in the database");
            return false;
        }
        String userId = (String)this.userDetails.get("_id");
        UserModel user = DB.findById(userId);

        if ( user == null){
            AppLogger.error(String.format("No user with the ID: %s was found",userId));
            return false;
        }

        if (!this.isUserLoggedIn){
            AppLogger.error(String.format("User not logged in. User must log in to continue"));
            return false;
        }
        return true;
    }

    private void customerOperations (){
        while (true) {
            // Util.clearConsole(true); // Clear screen before showing the customer page
            // showWelcomeMessage(this.userDetails.get("username"));

            String prompt = """ 
                    ╔══════════════════════════════════════════════════╗
                    ║                 OPERATIONS PAGE                  ║
                    ╠══════════════════════════════════════════════════╣
                    ║                 1. Create Account                ║
                    ║                 2. View Accounts                 ║
                    ║                 3. Deposit                       ║
                    ║                 4. Withdraw                      ║
                    ║                 5. Transfer                      ║
                    ║                 6. View Profile                  ║
                    ║                 7. Update Profile                ║
                    ║                 8. Update Password               ║
                    ║                 9. Back                          ║
                    ║                                                  ║
                                  -> Choose an option """;
            Integer[] allowedOptions = {1,2,3,4,5,6,7,8,9};                      
            int choice = Util.getIntInput(prompt, allowedOptions);

            switch (choice){
                case 1 -> createAccount();
                case 2 -> viewUserAccountDetails();
                case 3 -> depositAndWithdrawal("deposition");
                case 4 -> depositAndWithdrawal("withdrawal");
                case 5 -> transfers();
                case 6 -> viewProfile();
                case 7 -> updateProfile();
                case 8 -> updatePassword();
                case 9 -> {
                    return;
                }
            }
        }
    }
    
    private void adminOperations (){
        while (true) {
            // Util.clearConsole(true); // Clear screen before showing the customer page

            String prompt = """ 
                    ╔══════════════════════════════════════════════════╗
                    ║                 OPERATIONS PAGE                  ║
                    ╠══════════════════════════════════════════════════╣
                    ║                 1. Create Account                ║
                    ║                 2. View Accounts                 ║
                    ║                 3. Deposit                       ║
                    ║                 4. View Profile                  ║
                    ║                 5. Update Profile                ║
                    ║                 6. Update Password               ║
                    ║                 7. Veiw all Customers            ║
                    ║                 8  view all Accounts             ║
                    ║                 9. Back                          ║
                    ║                                                  ║
                                  -> Choose an option """;
            Integer[] allowedOptions = {1,2,3,4,5,6,7,8,9};                      
            int choice = Util.getIntInput(prompt, allowedOptions);

            switch (choice){
                case 1 -> createAccount();
                case 2 -> viewUserAccountDetails();
                case 3 -> depositAndWithdrawal("deposition");
                case 4 -> viewProfile();
                case 5 -> updateProfile();
                case 6 -> updatePassword();
                case 7 -> this.viewAllCustomers();
                case 8 -> this.viewAllAccounts();
                case 9 -> {
                    return;
                }
            }
        }
    }

    private UserModel find(HashMap<String, String> fields){
        if(DB.isEmpty()){ 
            AppLogger.error("No users in the database");
            return null;
        }
        for ( UserModel user: DB.getAll()){
            boolean allFieldsMatch = true;
            for (String key: fields.keySet()){
                if (key == null) {
                    AppLogger.error(String.format("All keys/fields of the filter must be provided; null keys not allowed"));
                    return null;
                }  // skip null keys
                String value = fields.get(key);
                String foundField = switch (key){
                    case "firstName" -> user.getFirstName();
                    case "lastName" -> user.getLastName();
                    case "username" -> user.getUsername();
                    case "email" -> user.getEmail();
                    case "role" -> user.getRole();
                    case "_id" -> user.getId();
                    default -> null;
                };
                if(foundField == null || !foundField.equals(value)){
                    allFieldsMatch = false;
                    break;
                }
            }
            if (allFieldsMatch){
                return user;
            }
        }
        return null;    
    }

    private void login(){
        if(DB.isEmpty()){
            AppLogger.error("No users in the database");
            return;
        }
        HashMap<String, String> loginData = new HashMap<>();
        String username,password;

        System.out.printf("\n\n%s$$$$$$$$$$$$$$$$$$$$$ USER LOGIN $$$$$$$$$$$$$$$$$$$$$%s\n",Colour.INFO,Colour.RESET);
        // AppLogger.info("\n*************************** User Login ********************************");

        username = Util.getStringInput("Enter your username", null);
        password = Util.getStringInput("Enter your password", null);

        loginData.put("username", username);
        loginData.put("password", password);

        boolean isValidLoginData = Validation.validateLogin(loginData);

        if (isValidLoginData){
            HashMap<String, String> filter = new HashMap<>();
            filter.put("username", username);
            if (isUserAdmin()){
                filter.put("role", "admin");
            }
            UserModel user = this.find(filter);

            if (user != null){
                if(user.verifyPassword(password)){
                    String message = String.format("User %s successfully logged in", user.getUsername());
                    AppLogger.success(message);
                    this.setIsUserLoggedIn(true);
                    this.setUserDetails(user);
                }else{
                    AppLogger.error("Invalid Password detected, please check and try again");
                }
            }else{
                AppLogger.error("No user with that username found, please check and try again");
            }
        }
        
    }

    private void logout(){
        if (this.isUserLoggedIn){
            this.setIsUserLoggedIn(false);
            showGoodBye(this.userDetails.get("username").toString());
            this.userDetails.clear();
        }else{
            AppLogger.error("You are already logged out");
        }
    }

    private void viewProfile(){
        if(!this.isAuthenticatedUser()){
            return;
        }
        UserModel user = DB.findById((String)this.userDetails.get("_id"));
        System.out.println(user);
    }

    private UserModel register(){
        String message;
        HashMap<String, String> registerData = new HashMap<>();
        String firstName, lastName, username, email, password;

        System.out.printf("\n\n%s$$$$$$$$$$$$$$$$$$$$$ NEW USER REGISTRATION $$$$$$$$$$$$$$$$$$$$$%s\n",Colour.INFO,Colour.RESET);
        // AppLogger.info("\n************************** New user registration ********************************");

        firstName = Util.getStringInput("Enter your first name",null);
        lastName = Util.getStringInput("Enter your last name", null);
        username = Util.getStringInput("Enter your username", null);
        email = Util.getStringInput("Enter your email", null);
        password = Util.getStringInput("Enter your password", null);

        registerData.put("firstName", firstName);
        registerData.put("lastName", lastName);
        registerData.put("username", username);
        registerData.put("email", email);
        registerData.put("password", password);
        if (isUserAdmin()){
            registerData.put("role", "admin");
        }

        boolean isValidRegisterData = Validation.validateRegistration(registerData);

        if (isValidRegisterData){
                if(isUserExist(username) || isUserExist(email)){
                message = String.format("A user already exists with that email or username, try a different one");
                AppLogger.error(message);
                return null;
            }else{
                UserModel newUser = isUserAdmin() ? new UserModel(firstName,lastName,username,password,email,"admin") : new UserModel(firstName,lastName,username,password,email);
                DB.create(newUser);
                message = String.format("%s registered successfully!\n", newUser.getUsername());
                AppLogger.warn(message);
                return newUser;
            }
        }
        return null;
    }

    // Helper method for updateProfile method
    private  void handleUpdate(String _id, HashMap<String, String> updates){
        UserModel user = DB.findById(_id);
        updates.forEach((key, value) ->{
            switch (key){
                case "firstName" -> user.setFirstName(value);
                case "lastName" -> user.setLastName(value);
                case "username" -> user.setUsername(value);
                case "email" -> user.setEmail(value);
                default -> {
                    String message = String.format("The update key/field : %s is not valid, valid fieds are %s.\n", key,Arrays.toString(Field.values()));
                    AppLogger.error(message);
                }
            }
        });
        DB.update(user);
        AppLogger.success("Profile update completed sucessfully");
    }
    // Main updateProfile method
    private void updateProfile(){
        if (!this.isAuthenticatedUser()){
            return;
        }
        String userId = (String)this.userDetails.get("_id");
        HashMap<String , String> updates = new HashMap<>();

        String prompt = """ 
                    ╔══════════════════════════════════════════════════╗
                    ║             WHAT DO YOU WANT TO UPDATE           ║
                    ╠══════════════════════════════════════════════════╣
                    ║                 1. First name                    ║
                    ║                 2. Last name                     ║
                    ║                 3. Username                      ║
                    ║                 4. Email                         ║
                    ║                 5. Back                          ║
                    ║                                                  ║
                                  -> Choose an option """;    
        outerLoop:
        while (true){
            Integer[] allowedOptions = {1,2,3,4};                      
            int choice = Util.getIntInput(prompt, allowedOptions);

            String newValue;
            switch (choice){
                case 1 -> {
                    newValue = Util.getStringInput("Enter your new first name", null);
                    updates.put("firstName", newValue);
                }
                case 2 -> {
                    newValue = Util.getStringInput("Enter your new last name", null);
                    updates.put("lastName", newValue);
                }
                case 3 -> {
                    newValue = Util.getStringInput("Enter your new username", null);
                    if (this.isUserExist(newValue)){
                        AppLogger.error(String.format("The username %s is already taken, please choose a different one", newValue));
                    }else{
                        updates.put("username", newValue);
                    }
                }
                case 4 -> {
                    newValue = Util.getStringInput("Enter your new email", null);
                    if (this.isUserExist(newValue)){
                        AppLogger.error(String.format("The email %s is already taken, please choose a different one", newValue));
                    }else{
                        updates.put("email", newValue);
                    }
                }
                case 5 -> {
                    break outerLoop;
                }
            }
            Character[] addUpdateOptions = {'Y','N','y','n'};
            char addUpdate = Character.toLowerCase(Util.getCharInput("Do you want to add another update? (Y/N) ", addUpdateOptions));
            if (addUpdate == 'n'){
                break;
            }
        }

        if(updates.isEmpty()){
            AppLogger.warn("No updates selected");
            return;
        }
        handleUpdate(userId ,updates);
    }

    private void updatePassword(){
        if(!this.isAuthenticatedUser()){
            return;
        }
        String userId = (String)this.userDetails.get("_id");
        UserModel user = DB.findById(userId);
        String message;

        String oldPassword = Util.getStringInput("Enter your current password ", null);
        if (!user.verifyPassword(oldPassword)){
            AppLogger.error(String.format("Wrong old password detected, please check and try again"));
            return;
        }
        String newPassword = Util.getStringInput("Enter new password ", null);
        user.setPassword(newPassword);
        DB.update(user);
        message = String.format("%s's password has been updated successfully.",user.getUsername());
        AppLogger.success(message);
    }

    // Helper method for the creatAccount method
    private void handleCreateAccount(String _id){
        UserModel user = DB.findById(_id);
        String message;
        if (user != null){
            if(user.getRole().equals("admin")){
            AppLogger.warn("Admins cannot have a bank account, only customers can. Please select a customer to proceed");
            }else{
                Character[] allowedOptions = {'y','n','Y','N'};
                char depositResponse = Util.getCharInput(String.format("Do you want to deposit an initial amount in the account? (Y/N)"), allowedOptions);
                BankAccount newBankAccount = accountRepository.create(user, depositResponse == 'y');
                if (newBankAccount != null){
                    user.addAccount(newBankAccount);
                    DB.update(user);
                    message = String.format("Successfully created checking and saving account for %s.\n",user.getUsername());
                    // user.displayAccounts();
                    AppLogger.success(message);
                }
            }
        }else{
            message = String.format("Customer with the ID %s not found!",_id);
            AppLogger.error(message);
        }
    }
    // Main createAccount method
    private void createAccount (){
        if (!this.isAuthenticatedUser()){
            return;
        }
        Util.clearConsole(false);
        showWelcomeMessage(this.userDetails.get("username").toString());
        String userId = (String)this.userDetails.get("_id");
        System.out.printf("\n\n%s$$$$$$$$$$$$$$ NEW BANK ACCOUNT CREATION $$$$$$$$$$$$$$%s\n",Colour.INFO,Colour.RESET);
        if (this.userRole.equals("admin")){
            String customerId = Util.getStringInput(String.format("Hey %s, enter the ID of the customer you want to create an account for",this.userDetails.get("username")), null);
            // Logger.debug(String.format("customer ID %d", customerId));
            this.handleCreateAccount(customerId);
        }else{
            this.handleCreateAccount(userId);
        }
    }

    // Helper method for the viewUserAccountDetails method
    private   void handleViewAccounts(String _id){
        UserModel user = DB.findById(_id);
        String message;
        if (user != null){
            if(user.getRole().equals("admin")){
                AppLogger.warn("Admins do not have a bank account so viewing any account is not possible, only customers have accounts. Please select a customer to proceed");
            }else{
                user.displayAccounts();
            }
        }else{
            message = String.format("Customer with the ID %s not found!",_id);
            AppLogger.error(message);
        }
    }
    // Main viewUserAccountDetails method
    private void viewUserAccountDetails (){
        if (!this.isAuthenticatedUser()){
            return;
        }
        String userId = (String)this.userDetails.get("_id");
        if (this.userRole.equals("admin")){
            String customerId = Util.getStringInput(String.format("Hey %s, enter the ID of the customer you want to view account details for",this.userDetails.get("username")), null);
            this.handleViewAccounts(customerId);
        }else{
            this.handleViewAccounts(userId);
        }

    }

    
    // Helper method for the depositAndWithdrawal method
    private void handleDepositAndWithdrawal(String _id, String action){
        action = action.toLowerCase();
        UserModel user = DB.findById(_id);
        String message;
        if (user != null){
            if(user.getRole().equals("admin")){
                AppLogger.warn(String.format("Admins do not have a bank account so %s  is not possible, only customers have accounts. Please select a customer to proceed",action));
            }else{
                BankAccount updatedAccount = accountRepository.depositAndWithdrawal(user, action);
                if (updatedAccount == null){
                    return;
                }
                for (BankAccount bankAccount : user.accounts){
                    if (updatedAccount.getId().equals(bankAccount.getId())){
                        bankAccount = updatedAccount;
                        user.setlastUpdated();
                    }
                }
            }
        }else{
            message = String.format("Customer with the ID %s not found!",_id);
            AppLogger.error(message);
        }
    }
    // Main depositAndWithdrawal Mthod
    private void depositAndWithdrawal(String action){
        if (!this.isAuthenticatedUser()){
            return;
        }
        String userId = (String)this.userDetails.get("_id");
        if (this.userRole.equals("admin")){
            String customerId = Util.getStringInput(String.format("Hey %s, enter the ID of the customer you for the %s",this.userDetails.get("username"), action), null);
            // Logger.debug(String.format("customer ID %d", customerId));
            this.handleDepositAndWithdrawal(customerId, action);
        }else{
            this.handleDepositAndWithdrawal(userId, action);
        }
    }

    // Methos to transfer between a user account
    private void transferToOwnAccount(){
        if (!this.isAuthenticatedUser()){
            return;
        }
        String userId = (String)this.userDetails.get("_id");
        UserModel user = DB.findById(userId);
        if (user != null){
            if(user.getRole().equals("admin")){
                AppLogger.error("Admins do not have a bank account so transfers are not possible, only customers have accounts. Please select a customer to proceed");
            }else{
                BankAccount updatedAccount = accountRepository.transferToOwnAccount(user);
                if (updatedAccount == null){
                    return;
                }
                boolean updated = false;
                for (BankAccount bankAccount : user.accounts){
                    if (bankAccount.getId().equals(updatedAccount.getId())){
                        bankAccount = updatedAccount;
                        bankAccount.setlastUpdated();
                        user.setlastUpdated();
                        updated = true;
                        break;
                    }
                }
                if (updated){
                    AppLogger.success(String.format("User Accounts Updated Successfully!!"));
                }else{
                    AppLogger.warn(String.format("User accounts update was not successful: the Bank Account was not found.\nBank Account: \n%s", updatedAccount));
                }
            }
        }else{
            AppLogger.error(String.format("Customer with the ID %s not found!",userId));
        }
    }

    // Methos to transfer from one user to another
    private void transferToOtherUser (){
        if (!this.isAuthenticatedUser()){
            return;
        }
        String userId = (String)this.userDetails.get("_id");
        UserModel sender = DB.findById(userId);
        if (sender != null){
            if(sender.getRole().equals("admin")){
                AppLogger.error("Admins do not have a bank account so transfers are not possible, only customers have accounts. Please select a customer to proceed");
            }else{
                try {
                    String otherUserUsername = Util.getStringInput("Enter the username of the receiver ",null);
                    if(otherUserUsername.equals(sender.getUsername())){
                        AppLogger.error("You must enter a username other than yours or perform transfer to your own account");
                        return;
                    }
                    HashMap<String , String> filter = new HashMap<>();
                    filter.put("username", otherUserUsername);
                    UserModel receiver = this.find(filter);

                    if(receiver == null){
                        AppLogger.error(String.format("No user with the username %s was found. Please check and try again",otherUserUsername));
                        return;
                    }
                    BankAccount[] usersUpdatedAccounts = accountRepository.transferToAnother(sender,receiver);

                    if (usersUpdatedAccounts != null){
                        DB.update(sender);
                        DB.update(receiver);
                    }
                } catch (Exception e) {
                    String errorClass = e.getClass().toString();
                    String errorMessage = e.getMessage();
                    String errorCause = e.getCause().toString();
                    String errorStack = Arrays.toString(e.getStackTrace());

                    AppLogger.error(String.format("NAME: %s\nMESSAGE: %s\nCAUSE: %s\nSTACK: %s", errorClass,errorMessage,errorCause,errorStack));
                    // return;
                }
            }
        }else{
            AppLogger.error(String.format("Customer with the ID %s not found!",userId));
        }
    }

    // Main transfers page method
    private void transfers(){
        String prompt = """ 
                    ╔══════════════════════════════════════════════════╗
                                       Transfer To:             
                    ╠══════════════════════════════════════════════════╣
                    ║                    1. Your account               ║
                    ║                    2. Someone else               ║
                    ║                    3. Cancel                     ║
                    ║                                                  ║
                                  -> Choose an option """;
        Integer[] options = {1,2,3};
        int transferSelection = Util.getIntInput(prompt, options);

        switch (transferSelection){
            case 1 -> this.transferToOwnAccount();
            case 2 -> this.transferToOtherUser();
            case 3 -> {
                break;
            }
        }
    }

    private void viewAllCustomers (){
        if (!this.isAuthenticatedUser()){
            return;
        }
        if(!this.isUserAdmin()){
            AppLogger.error("Protected Page!! Only admins can view this page");
            return;
        }

        try {
            ArrayList<UserModel> users = DB.getAll();
            int i = 0;
            for(UserModel user: users){
                if(user.getUsername().equals(this.userDetails.get("username"))){
                    continue;
                }
                ++i;
                System.out.printf("\n%s*****************\n USER %d \n*********************\n",Colour.ART,i);
                System.out.println(user);
                System.out.println(Colour.RESET);
            }
        } catch (Exception e) {
            String errorClass = e.getClass().toString();
            String errorMessage = e.getMessage();
            String errorCause = e.getCause().toString();
            String errorStack = Arrays.toString(e.getStackTrace());

            AppLogger.error(String.format("NAME: %s\nMESSAGE: %s\nCAUSE: %s\nSTACK: %s", errorClass,errorMessage,errorCause,errorStack));
            // return;
        }
    }

    private void viewAllAccounts(){
        if (!this.isAuthenticatedUser()){
            return;
        }
        if(!this.isUserAdmin()){
            AppLogger.error("Protected Page!! Only admins can view this page");
            return;
        }

        accountRepository.viewAllAccounts();
    }
}