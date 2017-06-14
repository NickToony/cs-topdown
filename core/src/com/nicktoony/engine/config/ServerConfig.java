package com.nicktoony.engine.config;

import com.nicktoony.engine.EngineConfig;

/**
 * Created by nick on 13/07/15.
 *
 * The ServerConfig is all configuration for the server. Note that all these values are sent
 * directly to the client. Please see the ServerConfig.md for an explanation of each value.
 *
 * The values below are the defaults. The defaults are automatically written to a file on
 * first run.
 */
public class ServerConfig {
    // Server settings
    public String sv_name = "Dev Server";
    public int sv_max_players = 16;
    public boolean sv_server_list = false;
    public int sv_bots = 0;
    public String sv_map = EngineConfig.MAPS[0];
    public String sv_mode = EngineConfig.MODES[0];

    // Map settings

    // Connection details
    public String sv_ip = "127.0.0.1";
    public int sv_port = 8457;
    public String sv_password = "";

    // Gameplay settings
    public boolean mp_player_collisions = true;
    public float mp_player_move_speed = 2;
    public int mp_freeze_time = 5;
    public int mp_round_time = 300;
    public int mp_victory_time = 5;
    public int mp_bot_engage_range = 400;
    public int mp_sound_range = 800;

    // Server rates
    public int sv_tickrate = 1;
    public int sv_lag_compensate = 30;

    // Client rates
    public int cl_tickrate = 5;
}