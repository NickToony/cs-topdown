package com.nicktoony.cstopdown.networking.packets.helpers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Nick on 14/06/2017.
 */
public class PlayerDetailsWrapper implements Json.Serializable {
    public int id;
    public String name = "";
    public int kills = 0;
    public int deaths = 0;
    public int ping = 0;
    public int team = 0;
    public boolean changed = false;

    public void setKills(int kills) {
        this.kills = kills;
        changed = true;
    }

    public void setName(String name) {
        this.name = name;
        changed = true;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        changed = true;
    }

    @Override
    public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("name", name);
        json.writeValue("kills", kills);
        json.writeValue("deaths", deaths);
        json.writeValue("ping", ping);
        json.writeValue("team", team);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        id = jsonData.getInt("id");
        name = jsonData.getString("name");
        kills = jsonData.getInt("kills");
        deaths = jsonData.getInt("deaths");
        ping = jsonData.getInt("ping");
        team = jsonData.getInt("team");
    }
}
