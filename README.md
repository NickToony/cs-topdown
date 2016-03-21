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

## Server Hosting
The game is developed such that the entire rendering engine may be disabled for the purposes of hosting. Target the Server module when compiling, which can then be run in either a no-GUI mode or a simple text-based GUI. Of course, you can also host a game using the full-rendering game on platforms that support it (all except HTML).

[Server Config Documentation](SERVERCONFIG.md)

## Building
1. Clone the repo 
	- `git clone git clone git@github.com:NickToony/cs-topdown.git`
2. Fetch the required submodules
	- `git submodule update --init --recursive`
3. Build your preferred platform
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
