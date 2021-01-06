package rpg.com.paifabio.graficos;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spritesheet {
	
	BufferedImage spriteSheet;
	private int tileWidth, tileHeight;
	
	public Spritesheet(String path) {
		this(path,16,16);
	}
	
	public Spritesheet(String path,int tileWidth,int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		
		try {
			spriteSheet = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {
			System.out.println("Falha ao carregar spritesheet:" + path);
			e.printStackTrace();
		}
	}
	
	public BufferedImage getSprite(int x, int y) {
		return spriteSheet.getSubimage(x, y, tileWidth, tileHeight);
	}
	
	public BufferedImage getSprite(int x, int y,int width,int height) {
		return spriteSheet.getSubimage(x, y, width, height);
	}
	
	public BufferedImage getSpriteByPosition(int x, int y) {
		return spriteSheet.getSubimage(x*tileWidth, y*tileHeight, tileWidth, tileHeight);
	}

}
