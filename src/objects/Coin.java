/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import nodebinding.BottomLeftBinding;
import nodebinding.NodeBinding;
import util.Sprite;
import static util.Sprite.COIN;
import util.Vector2D;

/**
 *
 * @author imsofa
 */
public class Coin extends GameObject{
    
    public Coin(Vector2D initialPosition) {
        super(Sprite.COIN, new BottomLeftBinding());
        setPosition(initialPosition);
    }
    
}
