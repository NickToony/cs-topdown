package com.nicktoony.cstd.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.backends.lwjgl.LwjglNet;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.nick.ant.towerdefense.serverlist.ServerlistConfig;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.host.Host;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.nicktoony.cstd.server.Main.log;
import static com.nicktoony.cstd.server.Main.logException;

class Server {
    private final int FPS = 1000/60;

    private Host host;
    private ServerConfig config;
    private boolean timerIsRunning = false;
    private ServerSocket serverSocket;
    private Timer timer;
    private List<Client> clientList = new ArrayList<>();

    public Server(ServerConfig config) {
        this.config = config;
        setup();
    }

    public Server() {
        log("Setting up");
        GameserverConfig.setConfig(new ServerlistConfig());

        File configFile = new File("server/config.json");
        if (!configFile.exists()) {
            log("Config file does not exist");
            log("Copying default config to location");
            try {
                FileUtils.copyFile(new File("server/default.json"), configFile);
            } catch (IOException e) {
                e.printStackTrace();
                log("Failed to copy config. Exiting.");
                return;
            }
        }

        try {
            config = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create()
                    .fromJson(new FileReader(configFile), ServerConfig.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log("Failed to load config file. Exiting.");
            return;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            log("Failed to parse config file. Exiting.");
            return;
        }

        setup();
    }

    private void setup() {
        // Load GDX stuffs
        loadGDX();

        // Server list
        host = new Host("A Game Server", 0, 16);

        log("Server started up");

        // Open a socket
        try {
            serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, config.getPort(), new ServerSocketHints());
        } catch (Exception e) {
            if (!logException(e)) {
                e.printStackTrace();
                log("Could not start server");
            }
            return;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                step();
            }
        }, FPS, FPS);
        timerIsRunning = true;

        // BETTER SOLUTION?!
        while (serverSocket != null) {

            try {
                Socket socket = serverSocket.accept(new SocketHints());

                clientList.add(new Client(socket));
            } catch (Exception e) {
                logException(e);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log("Some sort of sleep error occured.. ignoring.");
            }
        }
    }

    private void loadGDX() {
        Gdx.net = new LwjglNet();
    }

    private void step() {

    }

    public void dispose() {
        if (host != null) {
            host.stop();
        }
        if (timer != null) {
            timer.cancel();
            timerIsRunning = false;
        }
        if (serverSocket != null) {
            serverSocket.dispose();
        }
    }
}