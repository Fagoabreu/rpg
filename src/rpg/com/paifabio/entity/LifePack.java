package rpg.com.paifabio.entity;

import java.awt.image.BufferedImage;

import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.sound.Sound;

public class LifePack extends Entity{
	
	private int healPoints=20;

	public LifePack(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
	}
	
	public void effect(Player player) {
		if(player.heal(healPoints)) {
			Sound.heal.play();
			Game.getGame().entityList.remove(this);
		}
	}

}
