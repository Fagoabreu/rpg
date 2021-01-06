package rpg.com.paifabio.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tile {
	//public BufferedImage tile_floor = Game.spritesheet.getSprite(0,0,16,16);
	//public BufferedImage tile_wall = Game.spritesheet.getSprite(16,0,16,16);
	
	private BufferedImage sprite;
	private int x,y;
	
	public Tile(int x, int y, BufferedImage sprite) {
		this.x=x;
		this.y=y;
		this.sprite = sprite;
	}
	
	public void render (Graphics g) {
		g.drawImage(sprite, x-Camera.x, y-Camera.y, null);
	}
	
}
