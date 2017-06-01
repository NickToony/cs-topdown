Server Configuration
=======

This is the documentation of the server config. It describes the purpose of each setting, the expected values, and a recommended value.

Server Settings
---------------

**sv_name (string)**
The name of the server as it will appear in the server list. Even if you do not plan to make this server public, you should still set this value.
*Default*: "Dev Server"

**sv_max_players (integer)**
The maximum number of players that should be allowed to play at any one time. This includes spectators. You should set this value to any number greater than 0. Be aware that the higher the value, the more system resources are required.
*Default/Recommended*: 16

**sv_server_list (boolean)**
Whether this server should be shown on the public server list. You'll want to set this to false on a private server.
*Default*: true

Connection Details
-----
**sv_ip (string)**
**sv_port (integer)**
**sv_password (string)**

Gameplay Settings
----
**mp_player_collisions (boolean)**
**mp_player_move_speed (float)**

Server Rates
------

**sv_tickrate (integer)**
This defines the number of times the server should update clients on the position of other clients per second. Higher values gives much greater accuracy of player locations for clients, but costs more processing time and bandwidth.
*Recommended/Default (for LAN)*: 60
*Recommended (for Internet)*: 20

**sv_lag_compensate (integer)**

Defines the amount of compensation to allow for latency, in ms. The server will wait this amount of time before processing inputs. It is part of the system that ensures fairness and synchronization across clients. Having it too low will result in laggy gameplay.
*Recommended*: 100
(this feature is not currently included.)

