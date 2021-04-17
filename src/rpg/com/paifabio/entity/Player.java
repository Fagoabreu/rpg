package rpg.com.paifabio.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import rpg.com.paifabio.graficos.Spritesheet;
import rpg.com.paifabio.main.Game;
import rpg.com.paifabio.singletons.RandomSingleton;
import rpg.com.paifabio.sound.Sound;
import rpg.com.paifabio.world.Camera;
import rpg.com.paifabio.world.World;

public class Player extends Entity {
	
	public boolean right,left,up,down;
	public double speed=0.8;
	private double jumpSpeed =1.5f;
	
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
	
	private boolean enableJump=true;
	private boolean jump=false,isJumping =false,jumpUp=false;
	private double jumpFrames=30,jumpCur = 0;

	public boolean heal(int value) {
		if (life==maxLife)
			return false;
		
		this.setLife(this.life+=value);
		
		return true;
	}
	
	public void takeDamage(int value) {
		this.setLife(this.life-=value);
		this.isDamaged = true;
		
		if (this.getLife()==0) {
			//die
			Sound.death.play();
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
		Game.getGame().changeMousePointer("/crosshair.png");
	}
	
	public boolean hasArma() {
		return this.arma;
	}
	
	public double getLife() {
		return life;
	}
	
	public void setLife( double life) {
		if(life>maxLife) {
			this.life=maxLife;
		}else if(life<0) {
			this.life=0;
		}else {
			this.life=life;
		}
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
	
	public void setJump(boolean value) {
		this.jump=true;
	}
	
	public BufferedImage getCurImage() {
		return this.curAnim[index];
	}
	
	public Player(int x, int y, int width, int height,Spritesheet spritesheet) {
		this(x, y, width,  height,spritesheet,0,0,width,height);	
	}
	
	public Player(int x, int y, int width, int height,Spritesheet spritesheet,int maskX, int maskY,int maskW,int maskH) {
		super(x, y, width, height, spritesheet.getSprite(32, 0));
		super.setMask(maskX, maskY, maskW, maskH);
		
		//inicializa anima��es do player
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
		
		damagePlayer[0] = spritesheet.getSpriteByPosition(0, 5);
		damagePlayer[1] = spritesheet.getSpriteByPosition(1, 5);
		
		curAnim=idlePlayer;
		
		//inicializa anima��es de armas
		gunSprites = new BufferedImage[5];
		gunSprites[0] = spritesheet.getSpriteByPosition(8, 0);//direita
		gunSprites[1] = spritesheet.getSpriteByPosition(9, 0);//esquerda
		gunSprites[2] = spritesheet.getSpriteByPosition(8, 1);//cima direita
		gunSprites[3] = spritesheet.getSpriteByPosition(9, 1);//cima esquerda
		gunSprites[4] = spritesheet.getSpriteByPosition(7, 1);//hit
		gunSpriteIndex=0;
	}
	
	public void tick() {
		depth=2;
		
		//jump � a variavel que inicia o pulo
		if(jump && enableJump) { //criei o enablejump para desabilitar o pulo do personagem
			jump=false; //j� usei o flag posso desligalo evitando um novo pulo ap�s soltar o botao
			if(isJumping==false) {//verifica se o personagem n�o est� no meio de um pulo
				isJumping=true; //inicia o processo de pular
				jumpUp=true;    //inicia o pulo subindo
			}
		}
		
		if(isJumping) { //executa o pulo
			if(jumpUp) {//Jumpup = true siginifica que est� subindo
				jumpCur+=jumpSpeed;
				if(jumpCur >= jumpFrames) {//quando jumpCur passar a altura(jumpFrames) tem que descer
					jumpUp=false;
				}
			}else { //se o jumpup =falso o personagem est� caindo(substuindo o Jump Down)
				jumpCur-=jumpSpeed;
				if(jumpCur<=0) {//verifica se o personagem chegou ao ch�o e finaliza o pulo
					isJumping =false;
				}
			}
			z=jumpCur; //altera a altura do jogador
		}
		
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
		if(World.getWorld().isfree((int)xNext,this.getY(),this.getZ())){
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
		
		if(World.getWorld().isfree(this.getX(),(int) yNext,this.getZ())){
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
		
		int offsetX=width/2,offsetY=11;
		
		if(dirX==null) {
			dirY=0.0;
			if(gunSpriteIndex==0||gunSpriteIndex==2) {
				dirX= 1.0;
			}else {
				dirX=(-1.0);
			}
		}
		
		if(isDamaged) {
			dirX = 	RandomSingleton.getInstance().nextInt(3) -1.0;
			dirY =	RandomSingleton.getInstance().nextInt(3) -1.0;
		}
		
		new BulletShoot(this.getX()+offsetX-1,this.getY()+offsetY-1,2,2,null,dirX,dirY,4);
		dirX =null;
		dirY=null;
		ammo--;
		isShoot=false;
		return true;
	}
	
	@Override
	public void render (Graphics g) {
		this.drawSombra(g);
		
		//desenha o player
		g.drawImage(curAnim[index], this.getX()-Camera.x, this.getY()-Camera.y-this.getZ(),  null);
		if(arma) {
			//desenhaArma
			g.drawImage(gunSprites[gunSpriteIndex],this.getX()-Camera.x, this.getY()-Camera.y-this.getZ(),  null);
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
