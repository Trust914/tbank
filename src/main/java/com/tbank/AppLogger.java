package com.tbank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
public final  class AppLogger {

    private static final class LogModel {
        private final String logLevel;
        private final Colour logColour;
        private final String message;

        private  final LocalDateTime DATE_TIME = LocalDateTime.now();
        private  final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss" );
        private  final String TIME_STAMP = DATE_TIME.format(FORMAT);

        public LogModel(String logLevel, Colour logColour, String message){
            this.logLevel = logLevel;
            this.logColour = logColour;
            this.message = message;
        }

        @Override
        public final String toString(){
            String log = String.format("%s%s [%S]: %s%s",this.logColour, this.TIME_STAMP, this.logLevel, this.message, Colour.RESET);
            return log;
        }
    }
    
    private static final ArrayList<LogModel> LOGS = new ArrayList<>();

    private AppLogger(){
        throw new  AssertionError("Cannot instantiate Logger class");

    }

    public static final void error(String message){
        LogModel log = new LogModel("ERROR",Colour.ERROR, message);
        LOGS.add(log);
        System.out.println();
        System.out.println( log);
    }

    public static final void warn(String message){
        LogModel log = new LogModel("WARNING", Colour.WARNING, message);
        LOGS.add(log);
        System.out.println();
        System.out.println(log);
    }

    public static final void debug(String message){
        LogModel log = new LogModel("DEBUG", Colour.DEBUG, message);
        // LOGS.add(log);
        System.out.println(log);
    }

    public static final void info(String message){
        LogModel log = new LogModel("INFO", Colour.INFO, message);
        LOGS.add(log);
        System.out.println();
        System.out.println(log);
    }

    public static final void success(String message){
        LogModel log = new LogModel("SUCCESS", Colour.SUCCESS, message);
        LOGS.add(log);
        System.out.println();
        System.out.println(log);
    }

    public static final void veiwAllLogs(){
        System.out.println();
        for (LogModel log : LOGS){
            System.out.println(log);
        }
    }
}
