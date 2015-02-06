package com.nicktoony.cstd.server;

import java.util.Date;

public class Main {
    public static void main(String [ ] args) {
        Server server = new Server();

        server.dispose();

        return;
    }

    public static void log(String message) {
        System.out.println(new Date().toString() + " :: " + message);
    }


}