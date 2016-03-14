![](https://github.com/NickToony/cs-topdown/blob/master/screenshots/screenshot3.png)

CS Top Down
=====================
A quick adventure into the world of LibGDX: topdown 2D shooter based on the popular Counterstrike series.

##Networking
Recently rebuilt on a new cross-platform networking base I developed as part of another game. Most content can now be ran natively on a desktop or as a full HTML game within a modern web browser, with full networking support. Additionally, implementations for Android and iOS are included, however not tested due to lack of touch controls. This is achieved through a highly flexible PlatformProvider class, which allows each platform to specify native implementations through a simple interface.

##Server Hosting
The game is developed such that the entire rendering engine may be disabled for the purposes of hosting. Target the Server module when compiling, which can then be run in either a no-GUI mode or a simple text-based GUI. Of course, you can also host a game using the full-rendering game on platforms that support it (all except HTML).

##Current Features
- Collisions and movement using Box2D
- External map loading and rendering using Tiled Map Editor
- Skeletal based animation using Spine
- Lighting engine using Box2D Lights
