package com.tbank;

public enum Colour{
    ERROR("\u001B[31m"), // red
    WARNING("\u001B[33m"),// yellow
    DEBUG("\u001B[35m"),//mangenta
    SUCCESS ("\u001B[32m"),// green
    INFO("\u001B[34m"),// blue
    ART("\u001B[35m"),// purple
    RESET("\u001B[0m");// normal terminal  colour

    private final String colourCode;

    Colour(String colourCode){
        this.colourCode = colourCode;
    }

    @Override
    public String toString(){
        return this.colourCode;
    }

}