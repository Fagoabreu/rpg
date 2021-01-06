package rpg.com.paifabio.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import rpg.com.paifabio.graficos.Spritesheet;
import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.main.Sound;
import rpg.com.paifabio.world.Camera;
import rpg.com.paifabio.world.World;

public class Player extends Entity {
	
	public boolean right,left,up,down;
	public double speed=0.8;
	
	private int frames = 0,maxFrames=10, index = 0,curDamagedFrame=0,maxDamagedFrames=20;
	private boolean moved=false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage[] upPlayer;
	private BufferedImage[] downPlayer;
	private BufferedImage[] idlePlayer;
	private BufferedImage[] damagePlayer;
	private BufferedImage[] curAnim;
	
	private BufferedImage[] gunSprites;
	private int gunSpriteIndex;
	
	private boolean arma;
	
	private int ammo =0,maxAmmo=20;
	
	private boolean isDamaged =false;
	
	private boolean isShoot = false;
	private Double dirX=null,dirY=null;
	
	private double life=50,maxLife=50;

	public boolean heal(int value) {
		if (life==maxLife)
			return false;
		
		life+=value;
		if(life>maxLife) 
			life=maxLife;
		
		return true;
	}
	
	public void takeDamage(int value) {
		life-=value;
		isDamaged = true;
		
		if (life<=0) {
			//die
			Sound.death.play();
			life=0;
			Game.getGame().setGameOver();
			return;
		}else {
			Sound.hurt.play();
		}
	}
	
	public boolean addAmmo(int value) {
		if(ammo ==maxAmmo)
			return false;
		
		ammo+=value;
		if(ammo>maxAmmo) 
			ammo=maxAmmo;
		return true;
		
	}
	
	public void enableArma() {
		this.arma =true;
	}
	
	public double getLife() {
		return life;
	}
	
	public double getMaxLife() {
		return maxLife;
	}
	
	public int getAmmo() {
		return ammo;
	}
	
	public int getMaxAmmo() {
		return maxAmmo;
	}
	
	public void setShoot(boolean isShoot) {
		this.isShoot = isShoot;
		this.dirX=null;
		this.dirY=null;
	}
	public void setShoot(boolean isShoot,double dirX,double dirY) {
		this.isShoot = isShoot;
		this.dirX=dirX;
		this.dirY=dirY;
	}
	
	public Player(int x, int y, int width, int height,Spritesheet spritesheet) {
		this(x, y, width,  height,spritesheet,0,0,width,height);	
	}
	
	public Player(int x, int y, int width, int height,Spritesheet spritesheet,int maskX, int maskY,int maskW,int maskH) {
		super(x, y, width, height, spritesheet.getSprite(32, 0));
		super.setMask(maskX, maskY, maskW, maskH);
		
		//inicializa animações do player
		idlePlayer = new BufferedImage[8];
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		upPlayer = new BufferedImage[4];
		downPlayer = new BufferedImage[4];
		damagePlayer = new BufferedImage[2];
		
		downPlayer[0] = spritesheet.getSprite(32, 0);
		downPlayer[1] = spritesheet.getSprite(48, 0);
		downPlayer[2] = spritesheet.getSprite(64, 0);
		downPlayer[3] = spritesheet.getSprite(80, 0);
		
		upPlayer[0] = spritesheet.getSprite(32, 16);
		upPlayer[1] = spritesheet.getSprite(48, 16);
		upPlayer[2] = spritesheet.getSprite(64, 16);
		upPlayer[3] = spritesheet.getSprite(80, 16);
		
		rightPlayer[0] = spritesheet.getSprite(32, 32);
		rightPlayer[1] = spritesheet.getSprite(48, 32);
		rightPlayer[2] = spritesheet.getSprite(64, 32);
		rightPlayer[3] = spritesheet.getSprite(80, 32);
		
		leftPlayer[0] = spritesheet.getSprite(32, 48);
		leftPlayer[1] = spritesheet.getSprite(48, 48);
		leftPlayer[2] = spritesheet.getSprite(64, 48);
		leftPlayer[3] = spritesheet.getSprite(80, 48);
		
		idlePlayer[0] =	spritesheet.getSprite(32, 64);
		idlePlayer[1] =	spritesheet.getSprite(48, 64);
		idlePlayer[2] =	spritesheet.getSprite(64, 64);
		idlePlayer[3] =	spritesheet.getSprite(80, 64);
		idlePlayer[4] =	spritesheet.getSprite(32, 80);
		idlePlayer[5] =	spritesheet.getSprite(48, 80);
		idlePlayer[6] =	spritesheet.getSprite(64, 80);
		idlePlayer[7] =	spritesheet.getSprite(80, 80);
		
		damagePlayer[0] = spritesheet.getSpriteByPosition(0, 2);
		damagePlayer[1] = spritesheet.getSpriteByPosition(1, 2);
		
		curAnim=idlePlayer;
		
		//inicializa animações de armas
		gunSprites = new BufferedImage[5];
		gunSprites[0] = spritesheet.getSpriteByPosition(8, 0);//direita
		gunSprites[1] = spritesheet.getSpriteByPosition(9, 0);//esquerda
		gunSprites[2] = spritesheet.getSpriteByPosition(8, 1);//cima direita
		gunSprites[3] = spritesheet.getSpriteByPosition(9, 1);//cima esquerda
		gunSprites[4] = spritesheet.getSpriteByPosition(8, 2);//hit
		gunSpriteIndex=0;
	}
	
	public void tick() {
		
		//MOVIMENTA E TROCA ANIMAÇÃO
		moved = false;
		double xNext = x;
		double yNext = y;
		
		if(right) {
			moved = true;
			xNext+=speed;
			curAnim = rightPlayer;
			gunSpriteIndex=0;
		}else if(left) {
			moved = true;
			xNext-=speed;
			curAnim = leftPlayer;
			gunSpriteIndex=1;
		}
		if(World.getWorld().isfree((int)xNext,this.getY())){
			x=xNext;
			super.setMaskRectangle();
		}
		
		if(up) {
			moved = true;
			yNext-=speed;
			curAnim = upPlayer;
			if(gunSpriteIndex==0||gunSpriteIndex==4) {
				gunSpriteIndex=2;
			}else if(gunSpriteIndex==1) {
				gunSpriteIndex=3;
			}
		}else if(down) {
			moved = true;
			yNext+=speed;
			
			curAnim = downPlayer;
			if(gunSpriteIndex==2 || gunSpriteIndex==4) {
				gunSpriteIndex=0;
			}else if(gunSpriteIndex==3) {
				gunSpriteIndex=1;
			}
		}
		
		if(World.getWorld().isfree(this.getX(),(int) yNext)){
			y=yNext;
			super.setMaskRectangle();
		}
		
		if(!moved && curAnim != idlePlayer) {
			curAnim = idlePlayer;
			
			if(gunSpriteIndex==2) {
				gunSpriteIndex=0;
			}else	if(gunSpriteIndex==3) {
				gunSpriteIndex=1;
			}
		}
		
		if(isDamaged) {
			curAnim=damagePlayer;
			gunSpriteIndex=4;
			curDamagedFrame++;
			if (curDamagedFrame>=maxDamagedFrames) {
				isDamaged=false;
				curDamagedFrame =0;
			}
		}
		
		//cria projetil
		shoot();
		
		//CONTROLA A TROCA DE  FRAMES
		frames++;
		if(frames == maxFrames) {
			frames=0;
			index++;
		}
		
		if(index>=curAnim.length) {
			index=0;
		}
		
		//check collision with itens;
		checkCollisionItens();
		
		//Controle de camera
		Camera.x =Camera.clamp(this.getX() - Game.getGame().WIDTH/2,0,World.getWorld().width*16 -Game.getGame().WIDTH );
		Camera.y =Camera.clamp(this.getY() - Game.getGame().HEIGHT/2,0,World.getWorld().height*16 -Game.getGame().HEIGHT );
		
	}
	
	private boolean shoot() {
		if(!isShoot) {
			return false;
		}
		if(!arma || ammo<1) {
			isShoot=false;
			return false;
		}
		
		int offsetX,offsetY=11;
		if(gunSpriteIndex==0||gunSpriteIndex==2) {
			offsetX=12;
		}else {
			offsetX=(1);
		}
		
		if(dirX==null) {
			dirY=0.0;
			dirX= offsetX>6?1.0:-1.0;
		}
		
		if(isDamaged) {
			dirX = 	Game.getGame().getRandonInt(3)  -1.0;
			dirY =	Game.getGame().getRandonInt(3) 	-1.0;
		}
		
		new BulletShoot(this.getX()+offsetX,this.getY()+offsetY,2,2,null,dirX,dirY,4);
		dirX =null;
		dirY=null;
		ammo--;
		isShoot=false;
		return true;
	}
	
	@Override
	public void render (Graphics g) {
		g.drawImage(curAnim[index], this.getX()-Camera.x, this.getY()-Camera.y,  null);
		if(arma) {
			//desenhaArma
			g.drawImage(gunSprites[gunSpriteIndex],this.getX()-Camera.x, this.getY()-Camera.y,  null);
		}
		super.drawMaskRectangle(g, Color.blue);
	}
	
	private void checkCollisionItens(){
		List<Entity> entityList = Game.getGame().entityList;
		for (int i=0; i<entityList.size();i++) {
			Entity e = entityList.get(i);
			if( e instanceof LifePack && isColliding(e)) {
				LifePack lp = (LifePack)e;
				lp.effect(this);
			}
			
			if(e instanceof Ammo && isColliding(e)) {
				Ammo b = (Ammo)e;
				b.effect(this);
			}
			
			if(e instanceof Weapon && isColliding(e)) {
				Weapon b = (Weapon)e;
				b.effect(this);
			}
		}
	}
}
