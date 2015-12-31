package com.nicktoony.spacebattle.rooms.game.entities.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Created by hgreen on 14/09/14.
 */
public class UserPlayer extends Player{

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

        direction = calculateDirection((int) getRoom().getMouseX(), (int) getRoom().getMouseY());

        super.step();


    }

}
