package rpg.com.paifabio.entity;

import java.awt.image.BufferedImage;

import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.sound.Sound;

public class Weapon extends Entity{

	public Weapon(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
	}
	
	public void effect(Player p) {
		Sound.pick.play();
		p.enableArma();
		Game.getGame().entityList.remove(this);
	}

}
