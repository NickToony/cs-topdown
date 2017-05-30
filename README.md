![](https://github.com/NickToony/cs-topdown/blob/master/screenshots/screenshot3.png)

CS Top Down
=====================
A quick adventure into the world of LibGDX: topdown 2D shooter based on the popular Counterstrike series.

## Current Features
- Collisions and movement using Box2D
- External map loading and rendering using Tiled Map Editor
- Skeletal based animation using Spine
- Lighting engine using Box2D Lights
- Cross-platform multiplayer (Desktop, Web, Android, iOS)
- Lag compensating netcode

## Controls
- Movement: WASD
- Shoot: Left click
- Flashlight: F
- Reload: R
- Switch weapons: 1/2

## Play Now
You can grab recent builds of the server, desktop and html from my server:
- [Web](http://cstopdown.nick-hope.co.uk/) (Any modern browser)
- [Desktop Client](http://cstopdown.nick-hope.co.uk/desktop.jar) (Windows, OSX, Linux)
- [Server](http://cstopdown.nick-hope.co.uk/server.jar) (Windows, OSX, Linux)
- [Assets](http://cstopdown.nick-hope.co.uk/assets.zip) (NOTE: Required for desktop and server, unzip into same folder)

You can also see all active servers [here](http://gameservers.nick-hope.co.uk/game/view/1).

## Server Hosting
The server module may be ran with either no GUI, or a simple text-based GUI. Alternatively, you can host a server using the game client on platforms that support it (all except HTML).

[Server Config Documentation](SERVERCONFIG.md)

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
