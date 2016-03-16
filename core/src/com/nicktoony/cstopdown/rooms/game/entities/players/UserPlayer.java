package com.nicktoony.cstopdown.rooms.game.entities.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nicktoony.cstopdown.networking.packets.player.PlayerInputPacket;

/**
 * Created by hgreen on 14/09/14.
 */
public class UserPlayer extends Player{

    private int lastMove = 0;
    private int lastUpdate = 0;

    private long stepsPressed = 0;


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
            setLightOn(!isLightOn());
        }

        this.reloadKey = Gdx.input.isKeyPressed(Input.Keys.R);

        shootKey = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        direction = calculateDirection((int) getRoom().getMouseX(), (int) getRoom().getMouseY());

        // Check if changed movement keys
        int newMove = ((moveLeft ? 1 : 0) * 1000)
                + ((moveRight ? 1 : 0) * 100)
                + ((moveUp ? 1 : 0) * 10)
                + ((moveDown ? 1 : 0));
        if (newMove != lastMove || lastUpdate <= 0) {
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
            getRoom().getSocket().sendMessage(playerMovePacket);

            lastUpdate = getRoom().getSocket().getServerConfig().cl_tickrate;
            lastMove = newMove;

            if (moveUp) {
                stepsPressed = System.currentTimeMillis();
            } else if (stepsPressed > 0) {
                System.out.println("Client steps: " + (System.currentTimeMillis() - stepsPressed));
                stepsPressed = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            setPosition((getRoom().getMouseX() + getRoom().getMap().getCameraX() - getRoom().getMap().getCamera().viewportWidth/2),
                    ((Gdx.graphics.getHeight() - getRoom().getMouseY()) + getRoom().getMap().getCameraY() - getRoom().getMap().getCamera().viewportHeight/2));
        }

        lastUpdate -= 1;

        super.step(delta);


    }

}
