package com.tbank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccountModel implements IDB{
    
    private static final char CURRENCY = '$';
    
    private static long  accountNumberCounter = 2_999_999_999L;

    private final long accountNumber; // instance variable for an individual account number
    private final String accountType;
    
    private  String _id; // instance variable for an individual account ID
    private double balance = 0.0;
    private String lastUpdated;

    
    public AccountModel(String bankAccountId, String accountType){
        if(accountType.equals("checking")){
            this._id = String.format("%sA",bankAccountId);
        }else if(accountType.equals("saving")){
            this._id = String.format("%sB",bankAccountId);
        }
        this.accountNumber = ++accountNumberCounter;
        this.accountType = accountType;
        this.lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    @Override
    public  String getId(){
        return this._id;
    }

    public String getAccountType(){
        return this.accountType;
    }

    public double getBalance(){
        return this.balance;
    }

    public long getAccountNumber(){
        return this.accountNumber;
    }

    // Setters
    @Override
    public void setlastUpdated(){
        this.lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public void addToBalance(double amount){
        this.balance += amount;
    }

    public void deductFromBalance(double amount){
        this.balance -= amount;
    }

    public String accountDetails(boolean viewBalance){
        String details = String.format("\n_id: %s\nACCOUNT_TYPE: %s\nACCOUNT_NUMBER: %d",_id,this.getAccountType(),this.getAccountNumber());
        details = viewBalance ? details + String.format("\n%S_BALANCE: %c%.2f\nLAST_UPDATED: %s\n",this.getAccountType(),CURRENCY,this.getBalance(),this.lastUpdated) : details + String.format("\nLAST_UPDATED: %s\n", this.lastUpdated);
        return  details;
    }

    @Override 
    public String toString(){
        return String.format("\n_id: %s\nACCOUNT_TYPE: %s\nACCOUNT_NUMBER: %d\nLAST_UPDATED: %s",_id,this.getAccountType(),this.getAccountNumber(),this.lastUpdated);
    }
}
