package rpg.com.paifabio.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

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
			BufferedImage floorTile,
			BufferedImage wallTile,
			BufferedImage escadaTile,
			BufferedImage ammoSprite,
			BufferedImage lifepackSprite,
			Spritesheet spritesheet,
			List<Entity>entityList,
			List<Enemy>enemyList,
			Player player,
			int dificuldade,
			int totalEnemies,
			Random rand
			) {
		//setando parametros
		this.width=30;
		this.height=30;
		int[] pixels = new int[width * height];
		tiles = new Tile[pixels.length];
		layer2 = new Tile[pixels.length];
		
		int totalAmmo = totalEnemies/2;
		int totalLifepack = totalEnemies/3;
		
		
		//posicionando o player
		int playerX = 1;
		int playerY = 1;
		player.setX(playerX*tileSize);
		player.setY(playerY*tileSize);
		//adiciona objetos segundo layer
		layer2[playerX + playerY*width] = new FloorTile(playerX*tileSize, playerY*tileSize, escadaTile);
		
		
		//preenchendo o mapa com paredes
		for(int xx=0;xx<width;xx++) {
			for(int yy=0;yy<width;yy++) {
				tiles[xx+yy*width] = new Tile(xx*tileSize,yy*tileSize, wallTile); 
			}
		}
		
		//criando o chão
		for (int xx = 0; xx < this.width; xx++) {
			for (int yy = 0; yy < this.height; yy++) {
				tiles[xx+yy*this.width] = new WallTile(xx*tileSize,yy*tileSize, wallTile); 
			}
		}
		
		int dir=0;
		int xx=1,yy=1;
		tiles[xx+yy*width] = new FloorTile(xx*tileSize,yy*tileSize, floorTile);
		
		for (int i=0;i<1200;i++) {
			if(dir==0) {
				//direita
				if(xx<width-2) {
					xx++;
				}
			}else if(dir==1) {
				//esquerda
				if(xx>1) {
					xx--;
				}
			}else if(dir==2) {
				//baixo
				if(yy<height-2) {
					yy++;
				}
			}else if(dir==3) {
				//cima
				if(yy>1) {
					yy--;
				}
			}
			
			//altera direção chance 30%
			if(rand.nextInt(10)<3 ) {
				dir= rand.nextInt(4);
			}
			
			
			//adiciona inimigos no chão chance 2% até chegar ao maximo de inimigos
			if(Entity.calculateDistance(player.getX(), player.getY(), xx*tileSize, yy*tileSize)>20*tileSize 
					&& totalEnemies>0 
					&& rand.nextInt(50)<1) {
				totalEnemies--;
				TipoEnemy[] tiposInimigo= TipoEnemy.values();
				TipoEnemy tipoInimigo = tiposInimigo [rand.nextInt(tiposInimigo.length)];
				
				addEnemy(
						xx, 
						yy, 
						spritesheet,
						tipoInimigo,
						dificuldade,
						entityList,
						enemyList);
			}
			//adiciona municao proximo ao player 
			if(i==1 || (totalAmmo>0 && rand.nextInt(100)<1)) {
				totalAmmo--;
				addAmmo(xx, yy, ammoSprite, entityList);
			}
			//add healthpack
			if(i==2 || (totalLifepack>0 && rand.nextInt(100)<1)) {
				totalLifepack--;
				addLifePack(xx, yy, lifepackSprite, entityList);
			}
		
			
			
			tiles[xx+yy*width] = new FloorTile(xx*tileSize,yy*tileSize, floorTile); 
		}
		
		
		World.world=this;
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
					//preenche as entidades
					if(pixelAtual== 0xffff0000) {
						//enemy Esqueleto
						addEnemy(
								posX, 
								posY, 
								spritesheet,
								TipoEnemy.ESQUELETO,
								dificuldade,
								entityList,
								enemyList);
					}else if(pixelAtual== 0xffaa0000) {
						//enemy Lobo
						addEnemy(
								posX, 
								posY, 
								spritesheet,
								TipoEnemy.LOBO,
								dificuldade,
								entityList,
								enemyList);
						
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
						addAmmo(posX, posY, ammoSprite, entityList);
					}else if(pixelAtual== 0xffff00ff) {
						//lifepack
						addLifePack(posX, posY, lifepackSprite, entityList);
					}
				}
			}
			World.setWorld(this);
		} catch (IOException e) {
			System.out.println("Falha ao carregar a imagem do mapa");
			e.printStackTrace();
		}
	}
	
	public Tile getTile(int x, int y) {
		return tiles[x+(y*this.width)];
	}
	
	public boolean isWall(int x,int y) {
		//verifica o ponto estÃ¡ lovre com 1 tile de distancia
		int x1 = x /tileSize;
		int y1 = y /tileSize;
		return tiles[x1 + (y1*width)] instanceof WallTile;
	}
	
	public boolean isfree(int xNext,int yNext, int z) {
		//verifica o ponto estÃ¡ lovre com 1 tile de distancia
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
		int xstart=Camera.x >> 4; //operador >> remove casas binarias, neste caso ele reomo ve 4 casas equipalente a divisÃ£o por 16 sem resto
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
				
				if(layer2!=null && layer2.length>(posX + posY * width)) {
					tile = layer2[posX + posY * width];
					if(tile!=null)
						tile.render(g);
				}
			}
		}
	}
	
	public void renderMiniMap(int[] minimapaPixels, Player player) {
		for(int xx=0; xx<width;xx++) {
			for(int yy=0; yy<height;yy++) {
				int index = xx +(yy*width);
				if(tiles[index] instanceof WallTile ) {
					minimapaPixels[index] = 0x66ffffff;
				}else {
					minimapaPixels[index] =  0x66000000;
				}
			}
		}
		
		if(player!=null) {
			int playerX = player.getX()/tileSize;
			int playerY = player.getY()/tileSize;
			
			minimapaPixels[playerX+playerY*width] =  0xffcc0000;
		}
		
		
	}
	
	private void addEnemy(int posX,int posY,Spritesheet spritesheet, TipoEnemy tipoEnemy,int dificuldade,List<Entity>entityList,List<Enemy>enemyList) {
		//enemy
		Enemy en = new Enemy(posX*tileSize, posY*tileSize, tileSize, tileSize, spritesheet,tipoEnemy,dificuldade);
		entityList.add(en);
		enemyList.add(en);
	}
	
	private void addAmmo(int posX, int posY,BufferedImage ammoSprite, List<Entity>entityList) {
		Ammo ammo =new Ammo(posX*tileSize, posY*tileSize, tileSize, tileSize, ammoSprite);
		ammo.setMask(4, 4, 7, 12);
		entityList.add(ammo);
	}
	
	private void addLifePack(int posX, int posY,BufferedImage lpSprite, List<Entity>entityList) {
		LifePack lp =new LifePack(posX*tileSize, posY*tileSize, tileSize, tileSize, lpSprite);
		lp.setMask(4, 4, 7, 12);
		entityList.add(lp);
	}
}
