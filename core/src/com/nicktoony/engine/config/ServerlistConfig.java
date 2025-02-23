package com.nicktoony.engine.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nicktoony.gameserver.service.Callback;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.client.responses.ServersList;
import com.nicktoony.gameserver.service.host.APIResponse.CreateServer;
import com.nicktoony.gameserver.service.host.APIResponse.UpdateServer;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 13/07/15.
 */
public class ServerlistConfig extends GameserverConfig {

    private Json json;

    @Override
    public String getServerUrl() {
        return "https://gameservers.nick-hope.co.uk/api/";
    }

    @Override
    public String getGameAPIKey() {
        return "KjyUfbGuOsTSq2cbP05M7hiwnwkgtLqzsjanzNN5cnbJfn7uGrOQ7DeaLqWo";
    }

    @Override
    public void debugLog(String message) {
        System.out.println("GameserverServiceLog :: " + message);
    }

    @Override
    public long getUpdateRate() {
        return 1 * 60 * 1000;
    }

    @Override
    public long getChangedUpdateRate() {
        return 1 * 60 * 1000;
    }

    @Override
    public ServersList parseJsonForServerList(Reader reader) throws IOException {
        return getJson().fromJson(ServersList.class, reader);
    }

    @Override
    public CreateServer parseJsonForCreateServer(Reader reader) throws IOException {
        return getJson().fromJson(CreateServer.class, reader);
    }

    @Override
    public UpdateServer parseJsonForUpdateServer(Reader reader) throws IOException {
        return getJson().fromJson(UpdateServer.class, reader);
    }

    @Override
    public void performGetRequest(String url, final Callback callback) {

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .url(url).build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                callback.onSuccess(true, httpResponse.getStatus().getStatusCode(), httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {
                callback.onFailure();
            }

            @Override
            public void cancelled() {
                callback.onFailure();
            }
        });

    }

    @Override
    public void performPostRequest(String url, Map<String, String> data, final Callback callback) {

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .formEncodedContent(data)
                .url(url).build();

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                callback.onSuccess(true, httpResponse.getStatus().getStatusCode(), httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {
                callback.onFailure();
            }

            @Override
            public void cancelled() {
                callback.onFailure();
            }
        });
    }

    /**
     * Provides a Json instance
     * @return
     */
    public Json getJson() {
        // If it's not created yet
        if (json == null) {
            // create the new json instance
            json = new Json();
            // Set a custom serializer, as "Map" is abstract and created by the default
            json.setSerializer(Map.class, new Json.ReadOnlySerializer<Map>() {

                @Override
                public Map read(Json json, JsonValue jsonData, Class type) {
                    // Create the map (Note: it only works for <String, String[]>
                    Map<String, Object> map = new HashMap<String, Object>();
                    // for each entry
                    for (JsonValue entry = jsonData.child; entry != null; entry = entry.next) {
                        // Each entry is an arrange of strings, so we fetch that
                        try {
                            map.put(entry.name, json.readValue(entry.name, String.class, jsonData));
                        } catch (Exception e) {
                            map.put(entry.name, json.readValue(entry.name, String[].class, jsonData));
                        }
                    }
                    return map;
                }

            });

            json.setIgnoreUnknownFields(true);
        }
        return json;
    }
}