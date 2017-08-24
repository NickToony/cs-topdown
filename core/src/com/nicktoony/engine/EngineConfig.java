package com.nicktoony.engine;

import com.badlogic.gdx.math.Vector2;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;

/**
 * Created by Nick on 18/05/2016.
 */
public class EngineConfig {
    public static final float PIXELS_PER_METRE = 32;
    public static final float METRES_PER_PIXEL = 1 / PIXELS_PER_METRE;
    public static final int CELL_SIZE = 32;

    public static class Skins {
        public static String DEFAULT = "skins/default/uiskin.json";
        public static String TRACER = "skins/tracerui/tracer-ui.json";
        public static String SGX = "skins/sgxui/sgx-ui.json";
        public static String CLEAN = "skins/cleancrispyui/clean-crispy-ui.json";
    }

    public static String[] MAPS = new String[] {
        "de_dust2", "cstd_docks"
    };

    public static String[] MODES = new String[] {
            "TeamDeathmatch", "LastTeamStanding", "Zombies", "Left4Dead"
    };

    public static float toMetres(float pixels) {
        return pixels * METRES_PER_PIXEL;
    }

    public static Vector2 toMetres(Vector2 vector) {
        return new Vector2(toMetres(vector.x), toMetres(vector.y));
    }

    public static float toPixels(float metres) {
        return metres * PIXELS_PER_METRE;
    }

    public static Vector2 toPixels(Vector2 pointHit) {
        return new Vector2(toPixels(pointHit.x), toPixels(pointHit.y));
    }

    public static float angleBetweenPoints(Vector2 vecFrom, Vector2 vecTo){
        return (float) ((Math.atan2((vecTo.x - vecFrom.x),
                (vecTo.y - vecFrom.y)) * 180.0f / Math.PI) + 180f);
    }

    public static String getTeamName(int team) {
        switch (team) {
            case PlayerModInterface.TEAM_SPECTATE:
                return "Spectators";

            case PlayerModInterface.TEAM_CT:
                return "Counter-Terrorists";

            case PlayerModInterface.TEAM_T:
                return "Terrorists";

            default:
                return "Unknown";
        }
    }

    public static boolean isValidTeam(int team) {
        return (team >= PlayerModInterface.TEAM_SPECTATE && team <= PlayerModInterface.TEAM_T);
    }
}
