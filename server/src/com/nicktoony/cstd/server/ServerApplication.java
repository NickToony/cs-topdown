package com.nicktoony.cstd.server;

import com.nick.ant.towerdefense.server.CSTDServer;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ServerApplication {
    public static CSTDServer server;

    public static void main(String [ ] args) {

        if (args.length <= 0) {
            startNoUI();
        } else {
            startUI();
        }

        return;
    }

    private static void startNoUI() {
        server = new CSTDServer(new CSTDServer.Logger() {
            @Override
            public void log(String message) {
                logMessage(message);
            }

            @Override
            public void log(Exception exception) {
                exception.printStackTrace();
            }

        });

        server.dispose();
    }

    private static void startUI() {


        server = new CSTDServer(new ServerUI(new ServerUI.UIListener() {
            @Override
            public void onClose() {
                System.exit(0);
            }
        }));

        server.dispose();
    }


    public static void logMessage(String message) {
        System.out.println(new Date().toString() + " :: " + message);
    }


}