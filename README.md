![](https://github.com/NickToony/cs-topdown/blob/master/screenshots/screenshot4.png)

CS Top Down
=====================
An adventure into the world of LibGDX: A multiplayer top-down shooter with precision, inspired by the popular Counterstrike series.

## Current Features
- Collisions and movement using Box2D
- External map loading and rendering using Tiled Map Editor
    - Custom Map/Textures downloaded from game-servers when joining
- Skeletal based animation using Spine
- Lighting engine using Box2D Lights
- Cross-platform multiplayer (Desktop, Web, Android, iOS)
    - Entirely based upon WebSockets
- Reliable server-authoriative net-code: smooth, responsive and fast
- Moddable game-modes, weapons, sounds, graphics, maps
- Experimental 3D world with OpenGL (Hit F12 to toggle!)

## Default gamemodes (or make your own!)
- Team Deathmatch (config: "TeamDeathMatch")
    - Spawn with a random weapon, infinite respawns
- Last Team Standing (config: "LastTeamStanding")
    - Spawn with all weapons, be the last team alive
- Zombies (config: "Zombies")
    - Kill zombies, or become a zombie and consume the humans!
- Left 4 Dead (config: "Left4Dead")
    - It's human players vs endlessly spawning zombie bots!

## Controls
- Movement: WASD
- Shoot: Left click
- Flashlight: F
- Reload: R
- Switch weapons: 1/2/3/4/5
- Tab: Scoreboard
- ESC: Menu
- F12: Toggle 3D world

## Play Now
You can grab recent builds of the server, desktop and html from my server:
- [Web](https://cs-topdown.firebaseapp.com/) (Any modern browser)
- [Desktop](https://github.com/NickToony/cs-topdown/releases) (Windows, OSX, Linux)
- [Server](https://github.com/NickToony/cs-topdown/releases) (Windows, OSX, Linux) - see releases

You can also see all active servers [here](http://gameservers.nick-hope.co.uk/game/view/1).

## Server Hosting
The server module may be ran with either no GUI (default), or a simple text-based GUI. Alternatively, you can host a server within the game client on platforms that support it (all except HTML).

[Server Config Documentation](SERVERCONFIG.md)

Example CLI run: `java -jar Server.jar`

## Building
1. Clone the repo, then enter it
	- `git clone https://github.com/NickToony/cs-topdown.git`
	- `cd cs-topdown`
2. Fetch the required submodules
	- `git submodule update --init --recursive`
3. Build your preferred platform (requires Java)
	- Server: `./gradlew server:dist`
	- Desktop: `./gradlew desktop:dist`
	- Android: `./gradlew android:dist`
	- iOS: `./gradlew ios:dist`
	- HTML: `./gradlew html:dist`

## Contributors

- [HJGreen](https://github.com/HJGreen)


## Credits

- Reload! Gun sounds by Red Button Audio ([GameDevMarket](https://www.gamedevmarket.net/asset/reload-970/))
- Sentinel FPS GUI by vengeancemk1 ([GameDevMarket](https://www.gamedevmarket.net/asset/sentinel-fps-gui-4387/))
- Map graphics from CS2D ([Website](http://www.cs2d.com/))

Please see the Pro license regarding the sound/graphic resources used [here](https://www.gamedevmarket.net/terms-conditions/#pro-licence).

<img src="https://github.com/NickToony/cs-topdown/blob/master/screenshots/screenshot5.png" width="48%"><img src="https://github.com/NickToony/cs-topdown/blob/master/screenshots/screenshot6.png" width="48%">
