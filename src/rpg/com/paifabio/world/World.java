package rpg.com.paifabio.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import rpg.com.paifabio.entity.Ammo;
import rpg.com.paifabio.entity.Enemy;
import rpg.com.paifabio.entity.Entity;
import rpg.com.paifabio.entity.LifePack;
import rpg.com.paifabio.entity.Player;
import rpg.com.paifabio.entity.Weapon;
import rpg.com.paifabio.enums.TipoEnemy;
import rpg.com.paifabio.graficos.Spritesheet;
import rpg.com.paifabio.main.Game;

public class World {
	
	private Tile[] tiles;
	private Tile[] layer2;
	public int width,height;
	public int tileSize=16;
	
	private static World world;
	
	public static World getWorld() {
		return world;
	}
	
	public static void setWorld(World world) {
		World.world = world;
	}
	
	
	public World(
			String path, 
			BufferedImage floorTile, 
			BufferedImage wallTile,
			BufferedImage skyTile,
			BufferedImage escadaTile,
			BufferedImage weaponSprite,
			BufferedImage ammoSprite,
			BufferedImage lifepackSprite,
			Spritesheet spritesheet,
			List<Entity> entityList,
			List<Enemy> enemyList ,
			Player player,
			int dificuldade
			) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			width = map.getWidth();
			height = map.getHeight();
			//List<Entity> entityList = Game.getGame().entityList;
			//List<Enemy> enemyList = Game.getGame().enemyList;
			//Player player = Game.getGame().getPlayer();
			
			int[] pixels = new int[width * height];
			tiles = new Tile[pixels.length];
			layer2 = new Tile[pixels.length];
			
			map.getRGB(0, 0, map.getWidth(), height, pixels, 0, width);
			//passa pelo mapa todo
			for(int posX = 0; posX < width;posX++) {
				for(int posY =0;posY < height;posY++) {
					int index = posX + (posY * width);
					int pixelAtual = pixels[index];
					//preenche o fundo
					if(pixelAtual== 0xffffffff) {
						tiles[index] = new WallTile(posX*tileSize, posY*tileSize, wallTile);
					}else if(pixelAtual== 0xff000000) {
						tiles[index] = new FloorTile(posX*tileSize, posY*tileSize, floorTile);
					}else if(pixelAtual== 0xff00ffff) {
						tiles[index] = new FloorTile(posX*tileSize, posY*tileSize, skyTile);
					}else {
						tiles[index] = new FloorTile(posX*tileSize, posY*tileSize, floorTile);
					}
					
					//preenche layer de objetos (Segundo layer)
					if(pixelAtual== 0xff0000ff) {
						layer2[index] = new FloorTile(posX*tileSize, posY*tileSize, escadaTile);
					}
					//preenche a camanda acima
					if(pixelAtual== 0xffff0000) {
						//enemy
						Enemy en = new Enemy(posX*tileSize, posY*tileSize, tileSize, tileSize, spritesheet,TipoEnemy.ESQUELETO,dificuldade);
						entityList.add(en);
						enemyList.add(en);
					}else if(pixelAtual== 0xffaa0000) {
							//enemy
							Enemy en = new Enemy(posX*tileSize, posY*tileSize, tileSize, tileSize, spritesheet,TipoEnemy.LOBO,dificuldade);
							entityList.add(en);
							enemyList.add(en);
						
					}else if(pixelAtual== 0xff00ff00) {
						//weapon
						Weapon w = new Weapon(posX*tileSize, posY*tileSize, tileSize, tileSize, weaponSprite);
						w.setMask(0, 7, 16, 7);
						entityList.add(w);
					}else if(pixelAtual== 0xff0000ff) {
						//player
						player.setX(posX*tileSize);
						player.setY(posY*tileSize);
					}else if(pixelAtual== 0xffffff00) {
						//amo
						Ammo ammo =new Ammo(posX*tileSize, posY*tileSize, tileSize, tileSize, ammoSprite);
						ammo.setMask(4, 4, 7, 12);
						entityList.add(ammo);
					}else if(pixelAtual== 0xffff00ff) {
						//lifepack
						LifePack lp = new LifePack(posX*tileSize, posY*tileSize, tileSize, tileSize, lifepackSprite);
						lp.setMask(3, 5, 9, 11);
						entityList.add(lp);
					}
				}
			}
			
		} catch (IOException e) {
			System.out.println("Falha ao carregar a imagem do mapa");
			e.printStackTrace();
		}
	}
	
	public boolean isWall(int x,int y) {
		//verifica o ponto está lovre com 1 tile de distancia
		int x1 = x /tileSize;
		int y1 = y /tileSize;
		return tiles[x1 + (y1*width)] instanceof WallTile;
	}
	
	public boolean isfree(int xNext,int yNext, int z) {
		//verifica o ponto está lovre com 1 tile de distancia
		int x1 = xNext /tileSize;
		int y1 = yNext /tileSize;
		
		int x2 = (xNext+tileSize-1) /tileSize;
		int y2 = yNext /tileSize;
		
		int x3 = xNext /tileSize;
		int y3 = (yNext+tileSize-1) /tileSize;
		
		int x4 = (xNext+tileSize-1) /tileSize;
		int y4 = (yNext+tileSize-1) /tileSize;
		
		if( !(tiles[x1 + (y1*width)] instanceof WallTile ||
				tiles[x2 + (y2*width)] instanceof WallTile ||
				tiles[x3 + (y3*width)] instanceof WallTile ||
				tiles[x4 + (y4*width)] instanceof WallTile 		
				)) {
			return true;
		}
		
		if (z>0) {
			return true;
		}
		
		return false;
		
	}
	
	public void render(Graphics g) {
		int xstart=Camera.x >> 4; //operador >> remove casas binarias, neste caso ele reomo ve 4 casas equipalente a divisão por 16 sem resto
		int ystart=Camera.y >> 4;
		
		int xfinal=xstart + (Game.getGame().WIDTH >> 4);
		int yfinal=ystart + (Game.getGame().HEIGHT >> 4);
		
		for (int posX = xstart; posX <= xfinal; posX++) {
			for (int posY = ystart; posY <= yfinal; posY++) {
				
				if(posX<0 || posY<0 || posX>=width || posY>=height)
					continue;
				
				Tile tile = tiles[posX + posY * width];
				if(tile!=null)
					tile.render(g);
				
				tile = layer2[posX + posY * width];
				if(tile!=null)
					tile.render(g);
			}
		}
	}
}
