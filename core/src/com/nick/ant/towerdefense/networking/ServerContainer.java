package com.nick.ant.towerdefense.networking;

import com.nick.ant.towerdefense.networking.server.CSTDServer;
import com.nick.ant.towerdefense.networking.server.ServerUI;

/**
 * Created by Nick on 15/02/2015.
 */
public class ServerContainer {
    private static CSTDServer server;

    public static CSTDServer getInstance() {
        if (server == null) {
            server = new CSTDServer(new ServerUI(new ServerUI.UIListener() {
                @Override
                public void onClose() {
                    System.exit(0);
                }
            }));
        }

        return server;
    }

    public static void dispose() {
        if (server != null) {
            server.dispose();
        }
    }
}
