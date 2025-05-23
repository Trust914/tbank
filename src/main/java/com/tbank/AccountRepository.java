package com.tbank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public final class AccountRepository {
    public static final  DB<BankAccount> DB = new DB<>();
    private static final Integer[] ALLOWED_ACCOUNT_TYPES_OPTIONS = {1,2,3}; // 1-checking, 2-saving, 3-Exit
    private static final Character[] ALLOWED_YES_NO_RESPONSES = {'y','n', 'Y', 'N'};

    protected final BankAccount create (UserModel user, boolean willDeposit){
        String branchLocation = Util.getStringInput("Enter the branch location", null);
        BankAccount newBankAccount;
        if (willDeposit){
            double depositAmount = Util.getDoubleInput("Enter deposit amount", null);
            newBankAccount = new BankAccount(user, branchLocation,depositAmount);
            DB.create(newBankAccount);
        }else{
            newBankAccount = new BankAccount(user, branchLocation);
            DB.create(newBankAccount);
        }
        return newBankAccount;
    }

    protected final BankAccount findById(String accountId){
        try {
            BankAccount account = DB.findById(accountId);
            return account;
        } catch (Exception e) {
            String errorClass = e.getClass().toString();
            String errorMessage = e.getMessage();
            String errorCause = e.getCause().toString();
            String errorStack = Arrays.toString(e.getStackTrace());

            AppLogger.error(String.format("NAME: %s\nMESSAGE: %s\nCAUSE: %s\nSTACK: %s", errorClass,errorMessage,errorCause,errorStack));
            return null;
        }
    }

    protected final BankAccount findByUser(UserModel user){
        if(DB.isEmpty()){
            AppLogger.error("No accounts in the database");
            return null;
        }
        BankAccount foundAccount;
        for (BankAccount account : DB.getAll()){
            if(account.getUsername().equals(user.getUsername()) && account.getEmail().equals(user.getEmail())){
                foundAccount = account;
                return foundAccount;
            }
        }
        return null;
    }

    protected final BankAccount find(HashMap<String, Object> fields){
        if(DB.isEmpty()){
            AppLogger.error("No accounts in the database");
            return null;
        }
        if (fields == null){
            AppLogger.error("Filter cannot be empty");
            return null;
        }
        
        for ( BankAccount account: DB.getAll()){
            boolean allFieldsMatch = true;
            for (String key: fields.keySet()){
                if (key == null) {
                    AppLogger.error(String.format("All keys/fields of the filter must be provided; null keys not allowed"));
                    return null;
                }  // skip null keys
                Object value = fields.get(key);
                Object foundField = switch (key){
                    case "_id" -> account.getId();
                    // case "accountType" -> account.getAccountType();
                    case "branchLocation" -> account.getBranchLocation();
                    case "username" -> account.getUsername();
                    case "email" -> account.getEmail();
                    default -> null;
                };
                if(foundField == null || !foundField.equals(value)){
                    allFieldsMatch = false;
                    break;
                }
            }
            if (allFieldsMatch){
                return account;
            }
        }
        return null;    
    }

    protected final BankAccount getBankAccount (UserModel user){
        try {
            Integer[] allowedAccounts = new Integer[user.accounts.size()];
            for (int i=0; i<user.accounts.size(); i++){
                allowedAccounts[i] = i+1;
            }
            user.displayAccounts();
            int selectedAccount = Util.getIntInput(String.format("Select Account for the operation %s", Arrays.toString(allowedAccounts)), allowedAccounts);
            BankAccount bankAccount = user.accounts.get(selectedAccount-1);
            if(bankAccount == null){
                AppLogger.error(String.format("The bank account with the ID %d was not found, please check and try again",selectedAccount));
                return null;
            }
            return bankAccount;
        } catch (Exception e) {
            String errorClass = e.getClass().toString();
            String errorMessage = e.getMessage();
            String errorCause = e.getCause().toString();
            String errorStack = Arrays.toString(e.getStackTrace());

            AppLogger.error(String.format("NAME: %s\nMESSAGE: %s\nCAUSE: %s\nSTACK: %s", errorClass,errorMessage,errorCause,errorStack));
            return null;
        }
    }
    protected final AccountModel getIndividualAccountType (BankAccount bankAccount,UserModel user, String purpose){
        if (purpose.equals("withdrawal")){
            return bankAccount.getCheckingAccount(); // withdrawal is only possible from checking account
        }
        
        String selectAccoutTypePrompt = String.format(""" 
                    ╔══════════════════════════════════════════════════╗
                               Account Type For %s            
                    ╠══════════════════════════════════════════════════╣
                    ║                    1. Checking                   ║
                    ║                    2. Saving                     ║
                    ║                    3. Cancel                     ║
                    ║                                                  ║
                                  -> Choose an option """,purpose);
        // Integer[] allowedAccountTypes = {1,2,3};                          
        int choice = Util.getIntInput(selectAccoutTypePrompt,ALLOWED_ACCOUNT_TYPES_OPTIONS);
       
        AccountModel chosenAccount = switch (choice){
            case 1 -> bankAccount.getCheckingAccount();
            case 2 -> bankAccount.getSavingAccount();
            case 3 -> null;
            default -> {
                yield null;
            }
        };
        if (chosenAccount == null){
            return null;
        }
        // System.out.printf("selected account %s", chosenAccount);
        return chosenAccount;
    }

    protected final BankAccount depositAndWithdrawal(UserModel user,String action){
        action = action.toLowerCase();
        if(DB.isEmpty()){
            AppLogger.error("No accounts in the database");
            return null;
        }
        System.out.printf(""" 
                    ╔══════════════════════════════════════════════════╗
                    ║               Account for %s                     ║
                    ╠══════════════════════════════════════════════════╣
                     """,action);
        // user.displayAccounts();
        BankAccount bankAccount = this.getBankAccount(user);
        AccountModel actionAccount = this.getIndividualAccountType(bankAccount,user, action);

        double amount = Util.getDoubleInput(String.format("Enter the amount for %s ", action), null);
        if (amount <= 0){
            AppLogger.error(String.format("You can only %s an amount greater than zero.", action));
            return  null;
        }else{
            switch (action) {
                case "deposition" -> actionAccount.addToBalance(amount);
                case "withdrawal" -> {
                    if (actionAccount.getBalance() < amount){
                        AppLogger.error("Insufficient balance in account for the withdrawal");
                        return null;
                    }
                    actionAccount.deductFromBalance(amount);
                }
                default -> {
                    AppLogger.error(String.format("Invalid operation : %s is not supported", action));
                    return null;
                }
            }
            actionAccount.setlastUpdated();

            bankAccount.updateIndividualAccount(actionAccount);
            bankAccount.updateTotalBalance();
            bankAccount.setlastUpdated();

            DB.update(bankAccount);
            AppLogger.success(String.format("Amount %s of %.2f in the %s account was successful",action,amount,actionAccount.getAccountType()));
            return bankAccount;
        }
    }

    protected final HashMap<String, AccountModel> getSrcDestAccounts (HashMap<String, String> srcDestAccounts, BankAccount bankAccount){
        HashMap<String, AccountModel> transferAccounts = new HashMap<>();
        try {
            for (String where : srcDestAccounts.keySet()){
                AccountModel whereAccount;
                String value = srcDestAccounts.get(where);
                if (value == null){
                    AppLogger.error("You must specify source and destination account ('checking or saving')");
                    return null;
                }
                switch (where){
                    case "src" -> {
                        switch (value.toLowerCase()){
                            case "checking" -> {
                                whereAccount = this.findById(bankAccount.getId()).getCheckingAccount();
                                transferAccounts.put(where,whereAccount);
                            }
                            case "saving" -> {
                                whereAccount = this.findById(bankAccount.getId()).getSavingAccount();
                                transferAccounts.put(where,whereAccount);
                            }
                            default -> {
                                AppLogger.error(String.format("%s is not a valid source, it  must be checking or saving", value));
                                return null;
                            }
                        }
                    }
                    case "dest" -> {
                        switch (value){
                            case "checking" -> {
                                whereAccount = this.findById(bankAccount.getId()).getCheckingAccount();
                                transferAccounts.put(where, whereAccount);
                            }
                            case "saving" -> {
                                whereAccount = this.findById(bankAccount.getId()).getSavingAccount();
                                transferAccounts.put(where, whereAccount);
                            }
                            default -> {
                                AppLogger.error(String.format("%s is not a valid source, it  must be checking or saving", value));
                                return null;
                            }
                        }
                    }
                    default -> {
                        AppLogger.error(String.format("%s is not a valid source field, it  must be src or dest", value));
                        return null;
                    }
                }
            }
            return transferAccounts;
        } catch (Exception e) {
            String errorClass = e.getClass().toString();
            String errorMessage = e.getMessage();
            String errorCause = e.getCause().toString();
            String errorStack = Arrays.toString(e.getStackTrace());

            AppLogger.error(String.format("NAME: %s\nMESSAGE: %s\nCAUSE: %s\nSTACK: %s", errorClass,errorMessage,errorCause,errorStack));
            return null;
        }
    }

    protected  BankAccount transferToOwnAccount (UserModel user){
        if(DB.isEmpty()){
            AppLogger.error("No accounts in the database");
            return null;
        }

        BankAccount bankAccount = this.getBankAccount(user);
        if (bankAccount == null){
            AppLogger.error(String.format("No bank account for the user %s was found",user.getUsername()));
            return null;
        }

        HashMap<String, String> getFromTo = new HashMap<>();

        // Integer[] allowedAccountTypes = {1,2,3};                          
        int src = Util.getIntInput(getPrompt("Transfer from "),ALLOWED_ACCOUNT_TYPES_OPTIONS);

        
        switch (src) {
            case 1 -> {
                char cont = Character.toLowerCase((Util.getCharInput("You are transfering from checking to saving account, continue? (Y/N) ", ALLOWED_YES_NO_RESPONSES)));
                if (cont == 'y'){
                    getFromTo.put("src", "checking");
                    getFromTo.put("dest","saving");
                }else{
                    return null;
                }
            }
            case 2 -> {
                char cont = Character.toLowerCase((Util.getCharInput("You are transfering from saving to checking account, continue? (Y/N) ", ALLOWED_YES_NO_RESPONSES)));
                if (cont == 'y'){
                    getFromTo.put("src", "saving");
                    getFromTo.put("dest","checking");
                }else{
                    return null;
                }
            }

        }
        
        HashMap<String , AccountModel> transferAccounts = this.getSrcDestAccounts(getFromTo, bankAccount);
        if (transferAccounts == null){
            AppLogger.error(String.format("No source and destination accounts found for the transfer",user.getUsername()));
            return null;
        }
        System.out.println("SrcDestAccounts\n%s" + transferAccounts.toString());
        
        AccountModel srcAccount = transferAccounts.get("src");
        System.out.println("SOURCE\n" + srcAccount.accountDetails(true));
        AccountModel destAccount = transferAccounts.get("dest");
        System.out.println("DESTINATION\n" + destAccount.accountDetails(true));

        double amount = Util.getDoubleInput(String.format("Enter the amount to transfer "), null);
        if (amount <= 0){
            AppLogger.error(String.format("You cannot transfer a negative amount."));
            return  null;
        }else{
            try {
                if (srcAccount.getBalance() < amount){
                    AppLogger.error(String.format(String.format("Insufficient amount in your %s account balance",srcAccount.getAccountType())));
                    return  null;
                }
                srcAccount.deductFromBalance(amount);
                srcAccount.setlastUpdated();

                destAccount.addToBalance(amount);
                destAccount.setlastUpdated();

                bankAccount.setlastUpdated();
                bankAccount.updateTotalBalance();
                bankAccount.updateIndividualAccount(srcAccount);
                bankAccount.updateIndividualAccount(destAccount);
                bankAccount.setlastUpdated();
                DB.update(bankAccount);

                AppLogger.success(String.format("Transfer Successful!!\n FROM : \n%s\nTO :\n%s ",srcAccount,destAccount));
                return bankAccount;
            } catch (Exception e) {
                String errorClass = e.getClass().toString();
                String errorMessage = e.getMessage();
                String errorCause = e.getCause().toString();
                String errorStack = Arrays.toString(e.getStackTrace());

                AppLogger.error(String.format("Transfer of %.2f from your %s to %s was not successful\nACCOUNTS :\nSOURCE : %s\nDESTINATION : %s",amount,srcAccount.getAccountType(), destAccount.getAccountType(), srcAccount, destAccount));
                AppLogger.error(String.format("NAME: %s\nMESSAGE: %s\nCAUSE: %s\nSTACK: %s", errorClass,errorMessage,errorCause,errorStack));
                return null;
            }
        }

    }

    protected BankAccount[] transferToAnother (UserModel sender, UserModel receiver){
        if(DB.isEmpty()){
            AppLogger.error("No accounts in the database");
            return null;
        }

        HashMap<String, Object> filter = new HashMap<>();
        filter.put("username",receiver.getUsername());

        try {
            BankAccount receiverAccount = this.find(filter);
            if (receiverAccount == null){
                AppLogger.error(String.format("No bank account for the user %s was found",receiver.getUsername()));
                return null;
            }
            AccountModel receiverCheckingAccount = receiverAccount.getCheckingAccount();
            BankAccount[] updatedAccounts = new BankAccount[2];
            double amount = Util.getDoubleInput(String.format("Enter the amount to transfer "), null);
            if (amount <= 0){
                AppLogger.error(String.format("You cannot transfer a negative amount."));
                return  null;
            }else{
                BankAccount senderBankAccount = this.getBankAccount(sender);
                AccountModel senderCheckingAccount = senderBankAccount.getCheckingAccount();
                if (senderCheckingAccount.getBalance() < amount){
                    AppLogger.error(String.format(String.format("Insufficient amount in your %s account balance",senderCheckingAccount.getAccountType())));
                    return  null;
                }

                senderCheckingAccount.deductFromBalance(amount);
                senderCheckingAccount.setlastUpdated();
                senderBankAccount.updateIndividualAccount(senderCheckingAccount);
                senderBankAccount.updateTotalBalance();
                senderBankAccount.setlastUpdated();
                DB.update(senderBankAccount);

                receiverCheckingAccount.addToBalance(amount);
                receiverCheckingAccount.setlastUpdated();
                receiverAccount.updateIndividualAccount(receiverCheckingAccount);
                receiverAccount.updateTotalBalance();
                receiverAccount.setlastUpdated();
                DB.update(receiverAccount);

                updatedAccounts[0] = senderBankAccount;
                updatedAccounts[1] = receiverAccount;

                if (updatedAccounts == null){
                    AppLogger.error(String.format("Transfer of %.2f to %s was not successful\nACCOUNT : %s",amount,receiver.getUsername(),senderCheckingAccount));
                    return null;
                }
                AppLogger.success(String.format("Your transfer of %.2f to %s was successful",amount,receiver.getUsername()));
                return updatedAccounts;
            }
            // System.out.println(receiverAccount);
            // return null;
        } catch (Exception e) {
            String errorClass = e.getClass().toString();
            String errorMessage = e.getMessage();
            String errorCause = e.getCause().toString();
            String errorStack = Arrays.toString(e.getStackTrace());

            AppLogger.error(String.format("NAME: %s\nMESSAGE: %s\nCAUSE: %s\nSTACK: %s", errorClass,errorMessage,errorCause,errorStack));
            return null;
        }
        
    }

    protected void viewAllAccounts (){
        if(DB.isEmpty()){
            AppLogger.error("No accounts in the database");
            return;
        }
        try {
            ArrayList<BankAccount> accounts = DB.getAll();
            int i = 0;
            for(BankAccount account: accounts){
                System.out.printf("\n%s*****************\n ACCOUNT %d \n*********************\n",Colours.ART,i);
                System.out.println(account);
                System.out.println(Colours.RESET);
            }
        } 
        catch (NullPointerException e){
            AppLogger.error(String.format("No bank Accounts found"));
        }
        catch (Exception e) {
            String errorClass = e.getClass().toString();
            String errorMessage = e.getMessage();
            String errorCause = e.getCause().toString();
            String errorStack = Arrays.toString(e.getStackTrace());

            AppLogger.error(String.format("NAME: %s\nMESSAGE: %s\nCAUSE: %s\nSTACK: %s", errorClass,errorMessage,errorCause,errorStack));
            // return;
        }
    }

    protected String getPrompt (String arg){
        return String.format(""" 
                    ╔══════════════════════════════════════════════════╗
                                       %s             
                    ╠══════════════════════════════════════════════════╣
                    ║                    1. Checking                   ║
                    ║                    2. Saving                     ║
                    ║                    3. Cancel                     ║
                    ║                                                  ║
                                  -> Choose an option """, arg);
    }
}
