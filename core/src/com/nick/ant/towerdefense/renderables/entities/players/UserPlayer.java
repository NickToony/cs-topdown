package com.nick.ant.towerdefense.renderables.entities.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nick.ant.towerdefense.Game;

/**
 * Created by hgreen on 14/09/14.
 */
public class UserPlayer extends Player{

    float rotationSpeed = 0;
    float rotationAcceleration = 0.1f;

    @Override
    public void step(){
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

//        if (Game.CONTROL_SETTING == Game.CONTROL_KEYBOARD) {
//            boolean rotateLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
//            boolean rotateRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
//
//            if (rotateLeft || rotateRight) {
//                if (rotationSpeed < 2) {
//                    rotationSpeed += rotationAcceleration;
//                }
//            } else {
//                rotationSpeed = 0;
//            }
//
//            direction += (rotateLeft ? rotationSpeed : 0)
//                    + (rotateRight ? -rotationSpeed : 0);
//        } else {
            direction = calculateDirection((int) room.getMouseX(), (int) room.getMouseY());
//        }

        super.step();


    }

}
