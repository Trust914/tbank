package com.tbank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserModel implements IDB {
    private static int _idCounter = 0;
    
    private final String _id;
    protected  final ArrayList<BankAccount> accounts;  // Array of account pairs -> [checkingAccount, savingAccount]

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String role = "customer";
    private String lastUpdated;

    public UserModel (String firstName, String lastName,String username, String password, String email ){
        ++_idCounter;
        this._id = String.format("%d",_idCounter);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username.toLowerCase();
        this.password = password;
        this.email = email.toLowerCase();
        this.accounts = this.role.equals("customer") ? new ArrayList<>() : null;
        this.lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }
    public UserModel (String firstName, String lastName,String username, String password, String email, String role){
        ++_idCounter;
        this._id = String.format("%d",_idCounter);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username.toLowerCase();
        this.password = password;
        this.email = email.toLowerCase();
        this.role = role;
        this.accounts = this.role.equals("customer") ? new ArrayList<>() : null;
        this.lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    // Getters
    public String getFirstName(){
        return this.firstName;
    }
    
    public String getLastName(){
        return this.lastName;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public String getEmail(){
        return this.email;
    }
    
    public String getRole(){
        return this.role;
    }
    
    @Override
    public String getId(){
        return this._id;
    }

    // Setters
    public void setFirstName(String newFirstname){
        this.firstName = newFirstname;
    }
    public void setLastName(String newLastName){
        this.lastName = newLastName;
    }
    public void setUsername(String newUsername){
        this.username = newUsername;
    }
    public void setPassword(String newPassword){
        this.password = newPassword;
    }
    public void setEmail(String newEmail){
        this.email = newEmail;
    }

    @Override
    public void setlastUpdated(){
        this.lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public void addAccount(BankAccount newBankAccount){
        this.accounts.add(newBankAccount);
    }
    
    // Methods
    public boolean  verifyPassword(String password){
        return this.password.equals(password);
    }
    public void displayAccounts(){
        int i = 0;
        for (BankAccount bankAccount : this.accounts){
            ++i;
            System.out.printf("\n%s*****************\n ACCOUNT %d \n*********************\n",Colours.ART,i);
           
            System.out.printf("%s",bankAccount);
            System.out.println(Colours.RESET);
        }
    }

    @Override
    public String toString(){
        return String.format("\n_id: %s\nNAME: %S %S\nUSERNAME: %s\nEMAIL: %s\nROLE: %s\nLAST_UPDATED: %s",_id,this.firstName,this.lastName,this.username,this.email,this.getRole(),this.lastUpdated);
    }
}
