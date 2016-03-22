package com.nicktoony.cstopdown.rooms.game.entities.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nicktoony.cstopdown.networking.packets.WeaponWrapper;
import com.nicktoony.cstopdown.networking.packets.player.PlayerInputPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerSwitchWeapon;
import com.nicktoony.cstopdown.networking.packets.player.PlayerToggleLight;
import com.nicktoony.cstopdown.services.weapons.WeaponManager;

/**
 * Created by hgreen on 14/09/14.
 */
public class UserPlayer extends Player{

    private int lastMove = 0;
    private boolean lastShoot = false;
    private boolean lastReload = false;
    private long lastUpdate = 0;


    @Override
    public void step(float delta){
        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            this.moveUp = true;
        }else{
            this.moveUp = false;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            this.moveDown = true;
        }else{
            this.moveDown = false;
        }


        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            this.moveLeft = true;
        }else{
            this.moveLeft = false;
        }


        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            this.moveRight = true;
        }else{
            this.moveRight = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            // toggle light
            setLightOn(!isLightOn());

            // Update server
            PlayerToggleLight toggleLight = new PlayerToggleLight();
            toggleLight.timestamp = getRoom().getGameManager().getTimestamp();
            toggleLight.light = isLightOn();
            getRoom().getSocket().sendMessage(toggleLight);
        }

        this.reloadKey = Gdx.input.isKeyPressed(Input.Keys.R);

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            setPosition((getRoom().getMouseX() + getRoom().getMap().getCameraX()),
                    ((Gdx.graphics.getHeight() - getRoom().getMouseY()) + getRoom().getMap().getCameraY()));
        }


        if (!getRoom().getHud().getMouse()) {
            shootKey = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        }

        direction = calculateDirection((int) getRoom().getMouseX(), (int) getRoom().getMouseY());
        directionTo = direction;

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            setNextWeapon(0);
            // Tell server
            PlayerSwitchWeapon playerSwitchWeapon = new PlayerSwitchWeapon();
            playerSwitchWeapon.timestamp = getRoom().getGameManager().getTimestamp();
            playerSwitchWeapon.slot = 0;
            getRoom().getSocket().sendMessage(playerSwitchWeapon);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            setNextWeapon(1);
            // Tell client
            PlayerSwitchWeapon playerSwitchWeapon = new PlayerSwitchWeapon();
            playerSwitchWeapon.timestamp = getRoom().getGameManager().getTimestamp();
            playerSwitchWeapon.slot = 1;
            getRoom().getSocket().sendMessage(playerSwitchWeapon);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            setNextWeapon(2);
            // Tell client
            PlayerSwitchWeapon playerSwitchWeapon = new PlayerSwitchWeapon();
            playerSwitchWeapon.timestamp = getRoom().getGameManager().getTimestamp();
            playerSwitchWeapon.slot = 2;
            getRoom().getSocket().sendMessage(playerSwitchWeapon);
        }

        // Check if changed movement keys
        int newMove = ((moveLeft ? 1 : 0) * 1000)
                + ((moveRight ? 1 : 0) * 100)
                + ((moveUp ? 1 : 0) * 10)
                + ((moveDown ? 1 : 0));
        if (newMove != lastMove
                || lastShoot != shootKey
                || lastReload != reloadKey
                || lastUpdate <= getRoom().getGameManager().getTimestamp()) {
            // Send move packet
            PlayerInputPacket playerMovePacket = new PlayerInputPacket();
            playerMovePacket.moveLeft = moveLeft;
            playerMovePacket.moveRight = moveRight;
            playerMovePacket.moveUp = moveUp;
            playerMovePacket.moveDown = moveDown;
            playerMovePacket.direction = getDirection();
            playerMovePacket.timestamp = getRoom().getGameManager().getTimestamp();
            playerMovePacket.x = x;
            playerMovePacket.y = y;
            playerMovePacket.reload = reloadKey;
            playerMovePacket.shoot = shootKey;
            getRoom().getSocket().sendMessage(playerMovePacket);

            lastUpdate = getRoom().getGameManager().getTimestamp() + 1000/getRoom().getSocket().getServerConfig().cl_tickrate;
            lastMove = newMove;
            lastShoot = shootKey;
            lastReload = reloadKey;
        }

        super.step(delta);


    }

}
