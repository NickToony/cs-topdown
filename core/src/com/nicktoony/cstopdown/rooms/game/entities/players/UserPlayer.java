package com.nicktoony.cstopdown.rooms.game.entities.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nicktoony.cstopdown.networking.packets.player.PlayerSwitchWeapon;
import com.nicktoony.cstopdown.networking.packets.player.PlayerToggleLight;

/**
 * Created by hgreen on 14/09/14.
 */
public class UserPlayer extends Player{


    @Override
    public void step(float delta){
        // Check if changed movement keys
        if (this.isPlayerChanged()) {
        this.moveUp = Gdx.input.isKeyPressed(Input.Keys.W);
        this.moveDown = Gdx.input.isKeyPressed(Input.Keys.S);
        this.moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        this.moveRight = Gdx.input.isKeyPressed(Input.Keys.D);

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
            zoomKey = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
        }

//        direction = calculateDirection((int) getRoom().getMouseX(), (int) getRoom().getMouseY());
        directionTo = calculateDirection((int) getRoom().getMouseX(), (int) getRoom().getMouseY());
        mouseDistance = calculateDistance((int) getRoom().getMouseX(), (int) getRoom().getMouseY()) / (getRoom().getMap().getCamera().viewportHeight/2);
//        directionTo = direction;

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
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            setNextWeapon(3);
            // Tell client
            PlayerSwitchWeapon playerSwitchWeapon = new PlayerSwitchWeapon();
            playerSwitchWeapon.timestamp = getRoom().getGameManager().getTimestamp();
            playerSwitchWeapon.slot = 3;
            getRoom().getSocket().sendMessage(playerSwitchWeapon);
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            setNextWeapon(4);
            // Tell client
            PlayerSwitchWeapon playerSwitchWeapon = new PlayerSwitchWeapon();
            playerSwitchWeapon.timestamp = getRoom().getGameManager().getTimestamp();
            playerSwitchWeapon.slot = 4;
            getRoom().getSocket().sendMessage(playerSwitchWeapon);
        }




            getRoom().getSocket().sendMessage(constructUpdatePacket());
        }

        // We need PhysicsEntity to update the positions
        super.step(delta);


    }

}
