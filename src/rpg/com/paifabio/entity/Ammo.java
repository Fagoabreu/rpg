package rpg.com.paifabio.entity;

import java.awt.image.BufferedImage;

import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.sound.Sound;

public class Ammo extends Entity{
	int value=10;

	public Ammo(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
	}
	
	public void effect(Player p) {
		if(p.addAmmo(value)) {
			Sound.pick.play();
			Game.getGame().entityList.remove(this);
		}
	}

}
