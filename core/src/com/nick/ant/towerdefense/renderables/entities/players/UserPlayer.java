package com.nick.ant.towerdefense.renderables.entities.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.rooms.Room;
import com.nick.ant.towerdefense.renderables.rooms.RoomGame;

/**
 * Created by hgreen on 14/09/14.
 */
public class UserPlayer extends Player{

    public UserPlayer(int x, int y){
        super(x, y);
    }

    @Override
    public void render(SpriteBatch spriteBatch){

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

        direction = calculateDirection((int) room.getMouseX(), (int) room.getMouseY());
        System.out.println("Player: " + this.x + "," + this.y + " || Mouse: " + room.getMouseX() + "," + room.getMouseY());
        super.render(spriteBatch);
    }

}
