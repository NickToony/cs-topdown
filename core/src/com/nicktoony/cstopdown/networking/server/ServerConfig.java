package com.nicktoony.cstopdown.networking.server;

/**
 * Created by nick on 13/07/15.
 *
 * You can turn this into a packet in Kryonet
 */
public class ServerConfig {
    // Server settings
    public String sv_name = "Dev Server";
    public int sv_max_players = 100;
    public boolean sv_server_list = true;

    // Map settings

    // Connection details
    public String sv_ip = "127.0.0.1";
    public int sv_port = 8080;
    public String sv_password = "";

    // Gameplay settings
    public boolean mp_player_collisions = false;
    public int mp_player_update_rate = 1000;
    public float mp_player_move_speed = 2;
}