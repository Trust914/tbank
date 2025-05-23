package com.tbank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class BankAccount implements IDB {
    private static final char CURRENCY = '$';
    private static int _idCounter = 0;

    private final HashMap<String, String> userDetails = new HashMap<>();
    private final String branchLocation;


    private AccountModel checkingAccount;
    private AccountModel savingAccount;

    private double totalBalance = 0.0;
    private final String _id; // instance variable for an individual account ID
    private String lastUpdated;

    public BankAccount(UserModel user, String branchLocation, double initialBalance){
        ++_idCounter;
        this._id = String.format("TB%d",_idCounter);

        this.branchLocation = branchLocation;

        this.checkingAccount = new AccountModel( this._id ,"checking");
        this.savingAccount = new AccountModel(this._id, "saving");

        this.checkingAccount.addToBalance(initialBalance);
        this.updateTotalBalance();

        this.userDetails.put("_id", user.getId());
        this.userDetails.put("accountName", String.format("%S %S",user.getFirstName(), user.getLastName()));
        this.userDetails.put("username",user.getUsername());
        this.userDetails.put("email", user.getEmail());
        this.userDetails.put("role", user.getRole());

        this.lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public BankAccount(UserModel user, String branchLocation){
        ++_idCounter;
        this._id = String.format("TB%d",_idCounter);

        this.branchLocation = branchLocation;

        this.checkingAccount = new AccountModel(this._id,"checking");
        this.savingAccount = new AccountModel(this._id,"saving" );

        this.userDetails.put("_id", user.getId());
        this.userDetails.put("accountName", String.format("%S %S",user.getFirstName(), user.getLastName()));
        this.userDetails.put("username",user.getUsername());
        this.userDetails.put("email", user.getEmail());
        this.userDetails.put("role", user.getRole());

        this.lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

     @Override
    public  String getId(){
        return this._id;
    }

    public String getFirstName(){
        return this.userDetails.get("accountName").split(" ")[0];
    }

    public String getLastName(){
        return this.userDetails.get("accountName").split(" ")[1];
    }

    public String getUsername(){
        return this.userDetails.get("username");
    }

    public String getEmail(){
        return this.userDetails.get("email");
    }

    public String getUserRole(){
        return this.userDetails.get("role");
    }

    public String getBranchLocation(){
        return this.branchLocation;
    }

    public double getTotalBalance (){
        return this.totalBalance;
    }


     @Override
    public void setlastUpdated(){
        this.lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public final void updateTotalBalance(){
        this.totalBalance = this.checkingAccount.getBalance() + this.savingAccount.getBalance();
    }

    public AccountModel getCheckingAccount(){
        return this.checkingAccount;
    }
    
    public AccountModel getSavingAccount(){
        return this.savingAccount;
    }

    public void updateIndividualAccount(AccountModel account){
        if(account.getAccountType().equals("checking")){
            this.checkingAccount = account;
        }else if(account.getAccountType().equals("saving")){
            this.savingAccount = account;
        }
    }

    @Override
    public String toString(){
        String bankAccountDetails = String.format("\n_id: %s\nACCOUNT_NAME: %S\nUSERNAME: %S\nUSER_EMAIL: %S\nBRANCH_LOCATION: %S\nTOTAL_BALANCE: %s%.2f\nLAST_UPDATED: %s"
        ,this._id,this.userDetails.get("accountName"),this.userDetails.get("username"),this.userDetails.get("email"),this.branchLocation,CURRENCY,this.getTotalBalance(),this.lastUpdated);

        bankAccountDetails += String.format("\n########### CHECKING ACCOUNT DETAILS ############\n%s\n########### SAVING ACCOUNT DETAILS ############\n%s",this.getCheckingAccount().accountDetails(true),this.getSavingAccount().accountDetails(true));

        return bankAccountDetails;
    }

}