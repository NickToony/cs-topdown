Server Configuration
=======

This is the documentation of the server config. It describes the purpose of each setting, the expected values, and a recommended value.

Server Settings
---------------

**sv_name (string)**
The name of the server as it will appear in the server list. Even if you do not plan to make this server public, you should still set this value.
*Default*: "CS Server"

**sv_max_players (integer)**
The maximum number of players that should be allowed to play at any one time. This includes spectators. You should set this value to any number greater than 0. Be aware that the higher the value, the more system resources are required (for players too!).
*Default/Recommended*: 16

**sv_server_list (boolean)**
Whether this server should be shown on the public server list. You'll want to set this to false on a private server (unless you set a password!).
*Default*: false

**sv_bots (integer)**
How many bots should be on the server at any time. Each bot takes up a player spot, meaning you should set a higher sv_max_players than you have bots, otherwise no humans can join!
*Default*: 7

**sv_bot_prefix (string)**
The prefix on a bot's name to indicate it is a bot. For example, you could set the value to "BOT " (with trailing space).
*Default*: ""

**sv_map (string)**

**sv_mode (string)**

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
This defines the minimum number of times the server should update clients on the position of other clients per second. Higher values gives much greater accuracy of player locations for clients, but costs much more processing time and bandwidth.
**Note**: The actual number of updates varies greatly, as the server dynamically decides when to push more updates.
*Recommended/Default (for LAN)*: 1
*Recommended (for Internet)*: 1

**sv_lag_compensate (integer)**

Defines the amount of compensation to allow for latency, in ms. The server will wait this amount of time before processing inputs. This helps smooth out irregular ping or packet loss (which is expensive since we use TCP websockets!).
*Recommended*: 0

