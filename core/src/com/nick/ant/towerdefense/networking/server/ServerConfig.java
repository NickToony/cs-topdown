package com.nick.ant.towerdefense.networking.server;

import com.nick.ant.towerdefense.networking.packets.Packet;

/**
 * Created by Nick on 04/02/2015.
 */
public class ServerConfig extends Packet {
    // Server settings
    public String sv_name = "A brand new server";
    public int sv_max_players = 32;
    public boolean sv_server_list = true;

    // Map settings
    public String sv_map = "de_dust2";

    // Connection details
    public String sv_ip = "127.0.0.1";
    public int sv_port = 3453;
    public String sv_password = "";

    // Gameplay settings
    public boolean mp_player_collisions = false;
    public int mp_player_update_rate = 1000;
    public float mp_player_move_speed = 2;
}
