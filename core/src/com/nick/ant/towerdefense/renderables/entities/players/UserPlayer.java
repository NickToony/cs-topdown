package com.nick.ant.towerdefense.renderables.entities.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
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


        float fromX = Gdx.graphics.getWidth()/2;
        float fromY = Gdx.graphics.getHeight()/2;
        float toX = Gdx.input.getX();
        float toY = Gdx.input.getY();
        float toDirection = new Vector2(fromX, fromY).sub(new Vector2(toX, toY)).angle();
        System.out.println(fromX + "," + fromY + " :: " + toX + "," + toY + " :: " + toDirection);
        if (toDirection < 70) {
            direction += ( 1 - toDirection/70) * 3;
        } else if (toDirection > 110) {
            direction -= ( (toDirection-110)/70 ) * 3;
        }

        super.step();


    }

}
