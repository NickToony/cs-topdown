package com.nicktoony.cstopdown.server;

import com.badlogic.gdx.Gdx;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.nicktoony.cstopdown.config.ServerConfig;
import com.nicktoony.cstopdown.services.Logger;

import java.io.*;

/**
 * Created by nick on 19/07/15.
 */
public class ServerConfigLoader {
    private static Gson gson;

    /**
     * Singleton method generates the gson object for reading server config
     * @return
     */
    private static Gson getConfigGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .setPrettyPrinting()
                    .create();
        }

        return gson;
    }

    /**
     * Attempt to find the server config in a local file (server/config.json)
     * @return true on success
     */
    public static ServerConfig findConfig(Logger logger) {
        File configFile = Gdx.files.local("server/config.json").file();
        if (!configFile.exists()) {
            logger.log("Config file does not exist");
            logger.log("Creating new default config");
            logger.log(configFile.getAbsolutePath());
            try {
                Gdx.files.local("server").file().mkdirs();
                configFile.createNewFile();
                FileWriter fileWriter = new FileWriter(configFile);
                getConfigGson().toJson(new ServerConfig(), fileWriter);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                logger.log(e);
                logger.log("Failed to copy config. Exiting.");
                return null;
            }
        }

        try {
            return getConfigGson()
                    .fromJson(new FileReader(configFile), ServerConfig.class);
        } catch (FileNotFoundException e) {
            logger.log(e);
            logger.log("Failed to load config file. Exiting.");
            return null;
        } catch (JsonSyntaxException e) {
            logger.log(e);
            logger.log("Failed to parse config file. Exiting.");
            return null;
        }
    }
}
