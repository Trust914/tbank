package com.tbank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
public final  class AppLogger {

    private static final class LogModel {
        private final String logLevel;
        private final String logColour;
        private final String message;

        private  final LocalDateTime DATE_TIME = LocalDateTime.now();
        private  final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss" );
        private  final String TIME_STAMP = DATE_TIME.format(FORMAT);

        public LogModel(String logLevel, String logColour, String message){
            this.logLevel = logLevel;
            this.logColour = logColour;
            this.message = message;
        }

        @Override
        public final String toString(){
            String log = String.format("%s%s [%S]: %s%s",this.logColour, this.TIME_STAMP, this.logLevel, this.message, Colours.RESET);
            return log;
        }
    }
    
    private static final ArrayList<LogModel> LOGS = new ArrayList<>();

    private AppLogger(){
        throw new  AssertionError("Cannot instantiate Logger class");

    }

    public static final void error(String message){
        LogModel log = new LogModel("ERROR",Colours.ERROR, message);
        LOGS.add(log);
        System.out.println();
        System.out.println( log);
    }

    public static final void warn(String message){
        LogModel log = new LogModel("WARNING", Colours.WARNING, message);
        LOGS.add(log);
        System.out.println();
        System.out.println(log);
    }

    public static final void debug(String message){
        LogModel log = new LogModel("DEBUG", Colours.DEBUG, message);
        // LOGS.add(log);
        System.out.println(log);
    }

    public static final void info(String message){
        LogModel log = new LogModel("INFO", Colours.INFO, message);
        LOGS.add(log);
        System.out.println();
        System.out.println(log);
    }

    public static final void success(String message){
        LogModel log = new LogModel("SUCCESS", Colours.SUCCESS, message);
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
