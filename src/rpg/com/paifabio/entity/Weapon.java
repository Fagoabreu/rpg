package rpg.com.paifabio.entity;

import java.awt.image.BufferedImage;

import rpg.com.paifabio.main.Game;

public class Weapon extends Entity{

	public Weapon(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		// TODO Auto-generated constructor stub
	}
	
	public void effect(Player p) {
		p.enableArma();
		Game.getGame().entityList.remove(this);
	}

}
