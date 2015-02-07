package com.nicktoony.cstd.server;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Main {
    /**
     * Define a hashset of exceptions to ignore
     */
    public static final Set<String> ignoreExceptions = new HashSet<String>()
        {{
            add("Error accepting socket.");
        }};

    public static void main(String [ ] args) {
        CSTDServer CSTDServer = new CSTDServer();

        CSTDServer.dispose();

        return;
    }

    public static void log(String message) {
        System.out.println(new Date().toString() + " :: " + message);
    }

    public static boolean logException(Exception e) {
        if (!ignoreExceptions.contains(e.getMessage())) {
            e.printStackTrace();
            return true;
        }
        return false;
    }


}